package se.ifmo.databases.tutor.repositories;

import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Material;

@Repository
public class MaterialSourceRepository extends AbstractFilesystemSourceRepository<Material> {

  private static final String MATERIALS_SRC = "materials";

  @Override
  public String getSourceDirectory() {
    return MATERIALS_SRC;
  }
}
