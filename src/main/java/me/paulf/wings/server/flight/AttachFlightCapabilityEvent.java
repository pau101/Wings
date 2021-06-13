package me.paulf.wings.server.flight;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.Event;

public final class AttachFlightCapabilityEvent extends Event {
    private final AttachCapabilitiesEvent<Entity> event;

    private final Flight instance;

    private AttachFlightCapabilityEvent(AttachCapabilitiesEvent<Entity> event, Flight instance) {
        this.event = event;
        this.instance = instance;
    }

    public Entity getObject() {
        return this.event.getObject();
    }

    public void addCapability(ResourceLocation key, ICapabilityProvider cap) {
        this.event.addCapability(key, cap);
    }

    public Flight getInstance() {
        return this.instance;
    }

    public static AttachFlightCapabilityEvent create(AttachCapabilitiesEvent<Entity> event, Flight instance) {
        return new AttachFlightCapabilityEvent(event, instance);
    }
}
