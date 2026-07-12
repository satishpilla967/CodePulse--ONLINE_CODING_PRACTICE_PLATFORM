package org.patinanetwork.codepulse.common.ff.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code ff.*} feature flag values from configuration. Flag values are exposed via boolean {@code isX()}
 * getters and are read reflectively by {@code FeatureFlagManager}.
 */
@Component
@ConfigurationProperties(prefix = "ff")
@Getter
@Setter
public class FeatureFlagConfiguration {
    private boolean duels;
    private boolean userMetrics;
}
