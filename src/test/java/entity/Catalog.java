package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Catalog {

    private int id;
    private String name;
    private String description;
    @JsonProperty("remote_identifier")
    private String remoteIdentifier;
    private int status;
    @JsonProperty("legacy_identifier")
    private String legacyIdentifier;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("modified_at")
    private String modifiedAt;
}
