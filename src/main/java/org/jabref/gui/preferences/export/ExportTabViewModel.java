package org.jabref.gui.preferences.export;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import org.jabref.gui.commonfxcontrols.SortCriterionViewModel;
import org.jabref.gui.preferences.PreferenceTabViewModel;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.FieldFactory;
import org.jabref.model.metadata.SaveOrder;
import org.jabref.preferences.ImportExportPreferences;
import org.jabref.preferences.PreferencesService;

public class ExportTabViewModel implements PreferenceTabViewModel {

    // SaveOrderConfigPanel
    private final BooleanProperty exportInOriginalProperty = new SimpleBooleanProperty();
    private final BooleanProperty exportInTableOrderProperty = new SimpleBooleanProperty();
    private final BooleanProperty exportInSpecifiedOrderProperty = new SimpleBooleanProperty();
    private final ListProperty<Field> sortableFieldsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<SortCriterionViewModel> sortCriteriaProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    private final PreferencesService preferencesService;
    private final ImportExportPreferences importExportPreferences;

    public ExportTabViewModel(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
        this.importExportPreferences = preferencesService.getImportExportPreferences();
    }

    @Override
    public void setValues() {
        SaveOrder exportSaveOrder = importExportPreferences.getExportSaveOrder();
        switch (exportSaveOrder.getOrderType()) {
            case SPECIFIED -> exportInSpecifiedOrderProperty.setValue(true);
            case ORIGINAL -> exportInOriginalProperty.setValue(true);
            case TABLE -> exportInTableOrderProperty.setValue(true);
        }
        sortCriteriaProperty.addAll(exportSaveOrder.getSortCriteria().stream()
                                                   .map(SortCriterionViewModel::new)
                                                   .toList());

        List<Field> fieldNames = new ArrayList<>(FieldFactory.getCommonFields());
        fieldNames.sort(Comparator.comparing(Field::getDisplayName));
        sortableFieldsProperty.addAll(fieldNames);
    }

    @Override
    public void storeSettings() {
        SaveOrder newSaveOrder = new SaveOrder(
                SaveOrder.OrderType.fromBooleans(exportInSpecifiedOrderProperty.getValue(), exportInOriginalProperty.getValue()),
                sortCriteriaProperty.stream().map(SortCriterionViewModel::getCriterion).toList());
        preferencesService.getImportExportPreferences().setExportSaveOrder(newSaveOrder);
    }

    public BooleanProperty saveInOriginalProperty() {
        return exportInOriginalProperty;
    }

    public BooleanProperty saveInTableOrderProperty() {
        return exportInTableOrderProperty;
    }

    public BooleanProperty saveInSpecifiedOrderProperty() {
        return exportInSpecifiedOrderProperty;
    }

    public ListProperty<Field> sortableFieldsProperty() {
        return sortableFieldsProperty;
    }

    public ListProperty<SortCriterionViewModel> sortCriteriaProperty() {
        return sortCriteriaProperty;
    }
}
