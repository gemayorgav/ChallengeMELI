package com.hackerrank.sample.service;

import com.hackerrank.sample.exception.BadResourceRequestException;
import com.hackerrank.sample.exception.NoSuchResourceFoundException;
import com.hackerrank.sample.model.Model;
import com.hackerrank.sample.repository.ModelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ModelServiceImpl.
 * Utiliza Mockito para aislar la capa de servicio del repositorio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ModelServiceImpl - Tests unitarios de lógica de negocio")
class ModelServiceImplTest {

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelServiceImpl modelService;

    // ──────────────────────────────────────────────────────────────────
    // Helper
    // ──────────────────────────────────────────────────────────────────

    private Model buildModel(Long id) {
        Model model = new Model();
        model.setId(id);
        model.setName("Producto Test " + id);
        model.setDescription("Descripcion test");
        model.setCategory("Categoria");
        model.setPrice(new BigDecimal("100.00"));
        model.setBrand("Marca");
        model.setModel("M-" + id);
        model.setStock(5);
        model.setIsAvailable(true);
        model.setIsNew(true);
        return model;
    }

    // ──────────────────────────────────────────────────────────────────
    // createModel
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createModel - Modelo nuevo se persiste correctamente")
    void createModel_NewModel_CallsSave() {
        Model model = buildModel(1L);
        when(modelRepository.findById(1L)).thenReturn(Optional.empty());

        modelService.createModel(model);

        verify(modelRepository, times(1)).save(model);
    }

    @Test
    @DisplayName("createModel - ID ya existente lanza BadResourceRequestException")
    void createModel_DuplicateId_ThrowsBadResourceRequestException() {
        Model model = buildModel(1L);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));

        assertThatThrownBy(() -> modelService.createModel(model))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("same id");

        verify(modelRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────────────────────────
    // getModelById
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getModelById - ID existente retorna el modelo correcto")
    void getModelById_ExistingId_ReturnsModel() {
        Model model = buildModel(2L);
        when(modelRepository.findById(2L)).thenReturn(Optional.of(model));

        Model result = modelService.getModelById(2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Producto Test 2");
        verify(modelRepository).findById(2L);
    }

    @Test
    @DisplayName("getModelById - ID inexistente lanza NoSuchResourceFoundException")
    void getModelById_NonExistingId_ThrowsNoSuchResourceFoundException() {
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> modelService.getModelById(99L))
                .isInstanceOf(NoSuchResourceFoundException.class)
                .hasMessageContaining("No model with given id found");
    }

    // ──────────────────────────────────────────────────────────────────
    // updateModel
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateModel - Modelo existente se actualiza y guarda")
    void updateModel_ExistingModel_CallsSave() {
        Model model = buildModel(3L);
        model.setName("Nombre Actualizado");
        when(modelRepository.findById(3L)).thenReturn(Optional.of(model));

        modelService.updateModel(model);

        verify(modelRepository, times(1)).save(model);
    }

    @Test
    @DisplayName("updateModel - ID inexistente lanza NoSuchResourceFoundException")
    void updateModel_NonExistingId_ThrowsNoSuchResourceFoundException() {
        Model model = buildModel(99L);
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> modelService.updateModel(model))
                .isInstanceOf(NoSuchResourceFoundException.class);

        verify(modelRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────────────────────────
    // deleteModelById
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteModelById - ID existente invoca deleteById en el repositorio")
    void deleteModelById_ExistingId_CallsDeleteById() {
        Model model = buildModel(4L);
        when(modelRepository.findById(4L)).thenReturn(Optional.of(model));

        modelService.deleteModelById(4L);

        verify(modelRepository).deleteById(4L);
    }

    @Test
    @DisplayName("deleteModelById - ID inexistente lanza NoSuchResourceFoundException")
    void deleteModelById_NonExistingId_ThrowsNoSuchResourceFoundException() {
        when(modelRepository.findById(777L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> modelService.deleteModelById(777L))
                .isInstanceOf(NoSuchResourceFoundException.class);

        verify(modelRepository, never()).deleteById(any());
    }

    // ──────────────────────────────────────────────────────────────────
    // deleteAllModels
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAllModels - Invoca deleteAllInBatch en el repositorio")
    void deleteAllModels_CallsDeleteAllInBatch() {
        modelService.deleteAllModels();

        verify(modelRepository, times(1)).deleteAllInBatch();
    }

    // ──────────────────────────────────────────────────────────────────
    // getAllModels
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllModels - Retorna lista completa de modelos")
    void getAllModels_ReturnsAllModels() {
        List<Model> models = List.of(buildModel(1L), buildModel(2L), buildModel(3L));
        when(modelRepository.findAll()).thenReturn(models);

        List<Model> result = modelService.getAllModels();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Model::getId).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(modelRepository).findAll();
    }

    @Test
    @DisplayName("getAllModels - Repositorio vacío retorna lista vacía")
    void getAllModels_EmptyRepository_ReturnsEmptyList() {
        when(modelRepository.findAll()).thenReturn(List.of());

        List<Model> result = modelService.getAllModels();

        assertThat(result).isEmpty();
    }

    // ──────────────────────────────────────────────────────────────────
    // getAllModelsPaginated
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllModelsPaginated - Retorna Page con los modelos de la página solicitada")
    void getAllModelsPaginated_ReturnsRequestedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Model> content = List.of(buildModel(1L), buildModel(2L));
        Page<Model> page = new PageImpl<>(content, pageable, 2);
        when(modelRepository.findAll(pageable)).thenReturn(page);

        Page<Model> result = modelService.getAllModelsPaginated(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        verify(modelRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getAllModelsPaginated - Segunda página con pageSize=2 retorna modelos correctos")
    void getAllModelsPaginated_SecondPage_ReturnsCorrectContent() {
        Pageable pageable = PageRequest.of(1, 2);
        List<Model> content = List.of(buildModel(3L), buildModel(4L));
        Page<Model> page = new PageImpl<>(content, pageable, 5);
        when(modelRepository.findAll(pageable)).thenReturn(page);

        Page<Model> result = modelService.getAllModelsPaginated(pageable);

        assertThat(result.getContent()).extracting(Model::getId).containsExactly(3L, 4L);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.hasNext()).isTrue();
    }
}
