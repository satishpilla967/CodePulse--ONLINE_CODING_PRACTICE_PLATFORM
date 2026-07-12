package org.patinanetwork.codepulse.api.user.body;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileBody {

    @Size(min = 1, max = 64)
    private String nickname;

    /**
     * Either an external image URL, or a base64 data URI when the picture was uploaded from the user's
     * device (no object storage is configured for this deployment, so uploads are stored inline). Capped
     * well under the column's MEDIUMTEXT limit to keep a single avatar from being unreasonably large.
     */
    @Size(max = 2_000_000)
    private String profileUrl;
}
