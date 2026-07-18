package dev.unleashed.smp.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PluginConfigTest {

    @Test
    void defaultsApplied() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("general.default-language", "en_US");
        PluginConfigStub stub = new PluginConfigStub(yaml);
        assertEquals("en_US", stub.getString("general.default-language", "th_TH"));
        assertEquals("th_TH", stub.getString("missing.path", "th_TH"));
    }

    private static final class PluginConfigStub extends PluginConfig {
        private final YamlConfiguration yaml;
        PluginConfigStub(YamlConfiguration yaml) { super(null, "x.yml"); this.yaml = yaml; }
        @Override public YamlConfiguration getYaml() { return yaml; }
    }
}
