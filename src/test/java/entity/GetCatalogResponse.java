package entity;

import lombok.Data;

import java.util.List;

@Data
public class GetCatalogResponse {

    private boolean status;
    private List<Catalog> data;
}
