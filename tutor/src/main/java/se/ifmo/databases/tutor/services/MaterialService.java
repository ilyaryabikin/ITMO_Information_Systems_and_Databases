package se.ifmo.databases.tutor.services;

import java.io.InputStream;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Material;
import se.ifmo.databases.tutor.repositories.MaterialRepository;
import se.ifmo.databases.tutor.repositories.MaterialSourceRepository;

@Service
@Transactional
public class MaterialService extends AbstractService<Material, Long, MaterialRepository> {

  private final MaterialSourceRepository materialSourceRepository;

  @Autowired
  protected MaterialService(
      final MaterialRepository entityRepository,
      final MaterialSourceRepository materialSourceRepository) {
    super(entityRepository);
    this.materialSourceRepository = materialSourceRepository;
  }

  public Collection<Material> findAllByLessonId(final Long lessonId) {
    return entityRepository.findAllByLessonId(lessonId);
  }

  public Material saveEntity(
      final Material entityToSave, final String mimeType, final InputStream inputStream) {
    final String materialUuid = materialSourceRepository.save(mimeType, inputStream);
    entityToSave.setUuid(materialUuid);
    return entityRepository.save(entityToSave);
  }

  public String getSource(final Material material) {
    return materialSourceRepository.getPath(material.getUuid()).toString();
  }

  @Override
  public String getLoggingEntityName() {
    return "Material";
  }
}
