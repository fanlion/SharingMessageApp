package sharing.ui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DescTableRow {
	private BooleanProperty common;
	private BooleanProperty nuncommon;
	private StringProperty field;
	private StringProperty type;
	private StringProperty isNull;
	private StringProperty isKey;
	private StringProperty defaultVal;
	private StringProperty extra;

	public DescTableRow(boolean common, boolean numcommon, String field, String type,
			String isNull, String isKey, String defaultVal, String extra) {
		this.common = new SimpleBooleanProperty(common);
		this.nuncommon = new SimpleBooleanProperty(numcommon);
		this.field = new SimpleStringProperty(field);
		this.type = new SimpleStringProperty(type);
		this.isNull = new SimpleStringProperty(isNull);
		this.isKey = new SimpleStringProperty(isKey);
		this.defaultVal = new SimpleStringProperty(defaultVal);
		this.extra = new SimpleStringProperty(extra);

		this.common.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean oldval, Boolean newval) {
				//if "common"is selected, set "uncommon" status unselected
				if (newval == true) {
					nuncommonProperty().setValue(false);
				}
			}
		});

		this.nuncommon.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean oldval, Boolean newval) {
				//if "nuncommon" change to selected status, set "uncommon" status is unselected
				if (newval == true) {
					commonProperty().setValue(false);
				}
			}
		});
	}

	public BooleanProperty commonProperty() { return common; }
	public BooleanProperty nuncommonProperty() { return nuncommon; }
	public StringProperty fieldProperty() { return field; }
	public StringProperty typeProperty() { return type; }
	public StringProperty isNullProperty() { return isNull; }
	public StringProperty isKeyProperty() { return isKey; }
	public StringProperty defaultValProperty() { return defaultVal; }
	public StringProperty extraProperty() { return extra; }

	public boolean getCommon() {
		return common.get();
	}

	public boolean getNuncommon() {
		return nuncommon.get();
	}

	public String getField() {
		return field.get();
	}

}
