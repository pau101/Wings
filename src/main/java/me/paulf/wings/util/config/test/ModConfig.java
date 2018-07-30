package me.paulf.wings.util.config.test;

import me.paulf.wings.util.config.ObservableConfig;
import me.paulf.wings.util.config.Property;

public final class ModConfig extends ObservableConfig {
    private final Property<Integer> foo = this.property("foo", 8).comment("this does bar").add();

    public void setFoo(int value) {
        this.foo.set(value);
    }

    public int getFoo() {
        return this.foo.get();
    }

    public Property<Integer> fooProperty() {
        return this.foo;
    }

    public static void main(String[] args) {
        ModConfig config = new ModConfig();
        config.setFoo(50);
        System.out.println(config.properties());
    }
}