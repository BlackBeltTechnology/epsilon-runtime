package hu.blackbelt.epsilon.runtime.execution.model.excel;

import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetColumn;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetWorksheet;

public class ExcelColumn extends SpreadsheetColumn {

	public ExcelColumn(final SpreadsheetWorksheet worksheet, final int index) {
		super(worksheet, index);
	}

}
