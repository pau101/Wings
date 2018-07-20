package com.pau101.wings.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LiveTextureLoader {
	private LiveTextureLoader() {}

	private static final class Holder {
		private static final LiveTextureLoader INSTANCE = new LiveTextureLoader();
	}

	private final Logger logger = LogManager.getLogger(LiveTexture.class.getSimpleName());

	private final Path root = getRoot();

	@Nullable
	private Refresher refresher;

	public void bind(ResourceLocation resource) {
		load(resource).bindTexture(resource);
	}

	public ResourceLocation create(String resource) {
		return create(new ResourceLocation(resource));
	}

	public ResourceLocation create(ResourceLocation resource) {
		load(resource);
		return resource;
	}

	private TextureManager load(ResourceLocation resource) {
		return load(Minecraft.getMinecraft().getTextureManager(), resource);
	}

	private TextureManager load(TextureManager textureManager, ResourceLocation resource) {
		//noinspection ConstantConditions
		if (textureManager.getTexture(resource) == null) {
			LiveTexture tex = new LiveTexture(resource, getPath(resource));
			tex.load(textureManager);
			getRefresher().watch(tex);
		}
		return textureManager;
	}

	private Path getPath(ResourceLocation resource) {
		return root.resolve(Paths.get("assets", resource.getNamespace(), resource.getPath()));
	}

	private Refresher getRefresher() {
		if (refresher == null) {
			refresher = createRefresher();
		}
		return refresher;
	}

	private Refresher createRefresher() {
		try {
			LiveRefresher refresher = new LiveRefresher(FileSystems.getDefault().newWatchService());
			refresher.setName(LiveTexture.class.getSimpleName() + " Refresher");
			refresher.setDaemon(true);
			refresher.start();
			return refresher;
		} catch (Exception e) {
			log(Level.ERROR, "Unable to construct refresher, textures will not refresh", e);
			return Refresher.NULL;
		}
	}

	private Path getRoot() {
		Callable<Path> supplier;
		String errorMessage;
		String liveTextureRoot = System.getProperty("liveTextureRoot");
		if (Strings.isNullOrEmpty(liveTextureRoot)) {
			supplier = () -> Paths.get("").toAbsolutePath().resolveSibling(Paths.get("src", "main", "resources"));
			errorMessage = "Unable to locate resources directory, use system property \"-DliveTextureRoot=C:/path/to/src/main/resources\"";
		} else {
			supplier = () -> Paths.get(liveTextureRoot);
			errorMessage = "Unable to locate resources directory";
		}
		return getDirectory(supplier, errorMessage);
	}

	private Path getDirectory(Callable<Path> supplier, String errorMessage) {
		Path path = null;
		Exception cause = null;
		try {
			path = supplier.call();
		} catch (Exception e) {
			cause = e;
		}
		if (path != null && Files.isDirectory(path)) {
			return path;
		}
		throw new RuntimeException(errorMessage, cause);
	}

	private void log(Level level, String message, Object... params) {
		logger.log(level, message, params);
	}

	public static LiveTextureLoader instance() {
		return Holder.INSTANCE;
	}

	private final class LiveTexture extends AbstractTexture {
		private final ResourceLocation resource;

		private final Path file;

		private LiveTexture(ResourceLocation resource, Path file) {
			this.resource = resource;
			this.file = file;
		}

		private ResourceLocation getResource() {
			return resource;
		}

		private Path getFile() {
			return file;
		}

		@Override
		public void loadTexture(IResourceManager resourceManager) throws IOException {
			try (InputStream in = Files.newInputStream(getFile())) {
				TextureUtil.uploadTextureImageAllocate(getGlTextureId(), ImageIO.read(in), false, false);
			}
		}

		private void load(TextureManager textureManager) {
			textureManager.loadTexture(getResource(), this);
		}
	}

	private interface Refresher {
		Refresher NULL = t -> {};

		void watch(LiveTexture texture);
	}

	private static final class LiveRefresher extends Thread implements Refresher {
		private final WatchService watcher;

		private final Map<Path, LiveTexture> textures = Maps.newHashMap();

		private final Set<Path> watchingDirectories = Sets.newHashSet();

		private final BiMap<Path, WatchKey> directories = HashBiMap.create();

		private final Multimap<WatchKey, WatchBehavior> keys = HashMultimap.create();

		private LiveRefresher(WatchService watcher) {
			this.watcher = watcher;
		}

		private boolean watch(Path directory, WatchBehavior behavior) {
			try {
				keys.put(directories.computeIfAbsent(directory, this::register), behavior);
				return true;
			} catch (UncheckedIOException e) {
				instance().log(Level.WARN, "Skipping registration of \"{}\"", directory, e);
			}
			return false;
		}

		private WatchKey register(Path directory) throws UncheckedIOException {
			try {
				return directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public synchronized void watch(LiveTexture texture) {
			Path parent = texture.getFile().getParent();
			boolean canWatch = !watchingDirectories.add(parent);
			if (!canWatch) {
				canWatch = watch(parent, new RefreshBehavior(parent));
			}
			if (canWatch) {
				textures.put(texture.getFile(), texture);
			} else {
				instance().log(Level.WARN, "Unable to watch \"{}\"", texture.getResource());
			}
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException e) {
					interrupt();
					break;
				}
				process(key);
			}
		}

		private synchronized void process(WatchKey key) {
			try {
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent<Path> ev = castEvent(event);
					Path context = ev.context();
					WatchEvent.Kind<Path> kind = ev.kind();
					Collection<WatchBehavior> behaviors = keys.get(key);
					behaviors.removeIf(b -> b.process(context, kind));
					if (behaviors.isEmpty()) {
						directories.inverse().remove(key);
						key.cancel();
					}
				}
			} finally {
				if (!key.reset()) {
					keys.removeAll(key).forEach(WatchBehavior::invalidate);
				}
			}
		}

		private <T> WatchEvent<T> castEvent(WatchEvent<?> event) {
			//noinspection unchecked
			return (WatchEvent<T>) event;
		}

		private abstract class WatchBehavior {
			protected final Path directory;

			protected WatchBehavior(Path directory) {
				this.directory = directory;
			}

			protected abstract boolean process(Path context, WatchEvent.Kind<Path> kind);

			protected final void invalidate() {
				Path parent = directory.getParent();
				boolean fail = parent == null;
				if (!fail) {
					fail = !watch(parent, new RecaptureDirectoryBehavior(parent, directory.getFileName(), this));
				}
				if (fail) {
					instance().log(Level.WARN, "Unable to watch for return of \"{}\"", directory);
				}
			}
		}

		private final class RefreshBehavior extends WatchBehavior {
			private RefreshBehavior(Path directory) {
				super(directory);
			}

			@Override
			protected boolean process(Path context, WatchEvent.Kind<Path> kind) {
				if (kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_CREATE) {
					textures.computeIfPresent(directory.resolve(context), (p, tex) -> scheduleRefresh(tex));
				}
				return false;
			}

			private LiveTexture scheduleRefresh(LiveTexture texture) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> texture.load(mc.getTextureManager()));
				return texture;
			}
		}

		private final class RecaptureDirectoryBehavior extends WatchBehavior {
			private final Path name;

			private final WatchBehavior behavior;

			private RecaptureDirectoryBehavior(Path directory, Path name, WatchBehavior behavior) {
				super(directory);
				this.name = name;
				this.behavior = behavior;
			}

			@Override
			protected boolean process(Path context, WatchEvent.Kind<Path> kind) {
				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					if (name.equals(context)) {
						watch(directory.resolve(context), behavior);
						return true;
					}
				}
				return false;
			}
		}
	}
}
