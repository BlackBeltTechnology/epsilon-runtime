package hu.blackbelt.epsilon.runtime.execution.model.excel;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.poi.ss.usermodel.*;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetColumn;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetDataType;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetRow;

public class ExcelRow extends SpreadsheetRow {
	protected ExcelWorksheet worksheet;
	protected Row row;

	public ExcelRow(final ExcelWorksheet worksheet, final Row row) {
		super(worksheet);
		this.worksheet = worksheet;
		this.row = row;
	}

	@Override
	public String getVisibleCellValue(final SpreadsheetColumn column) {
		super.validateColumn(column);

		String visibleCellValue = "";
		final Cell cell = this.row.getCell(column.getIndex());
		if (cell != null) {
			final FormulaEvaluator evaluator = worksheet.model.workbook.getCreationHelper().createFormulaEvaluator();
			final CellValue cellValue = evaluator.evaluate(cell);
			if (cellValue != null) {
				switch (cellValue.getCellType()) {
					case NUMERIC:
						double numericValue = cellValue.getNumberValue();
						if (column.getDataType() == SpreadsheetDataType.INTEGER) {
							visibleCellValue += Math.round(numericValue);
						}
						else if (column.getDataType() == SpreadsheetDataType.FLOAT) {
							visibleCellValue += new Float(numericValue);
						}
						else {
							visibleCellValue += numericValue;
						}
						break;
					case STRING:
						visibleCellValue = cellValue.getStringValue();
						break;
					case BOOLEAN:
						visibleCellValue += cellValue.getBooleanValue();
						break;
					default: visibleCellValue = cellValue.getStringValue();
				}
			}
			else {
				visibleCellValue += cell.getStringCellValue();
			}
		}
		return visibleCellValue;
	}

	@Override
	public void overwriteCellValue(final SpreadsheetColumn column, final String value) {
		super.validateColumn(column);
		final Cell cell = this.row.getCell(column.getIndex());
		
		if (this.row.getRowNum() == 0) {
			cell.setCellValue(value);
		}
		else {
			if (value != null && value.startsWith("=")) {
				cell.setCellFormula(value.substring(1));
			}
			else {
				if (column.isMany()) {
					cell.setCellValue(value);
				}
				else {
					switch (column.getDataType()) {
					case BOOLEAN: cell.setCellValue(Boolean.parseBoolean(value)); break;
					case INTEGER: cell.setCellValue(Integer.parseInt(value)); break;
					case FLOAT: cell.setCellValue(Float.parseFloat(value)); break;
					case DOUBLE: cell.setCellValue(Float.parseFloat(value)); break;
					default: cell.setCellValue(value);
					}
				}
			}
		}
	}
	
	public Row getRow() {
		return row;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + row.getRowNum();
		result = prime * result + ((worksheet == null) ? 0 : worksheet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExcelRow other = (ExcelRow) obj;
		if (row.getRowNum() != other.row.getRowNum()) {
			return false;
		}
		if (worksheet == null) {
			if (other.worksheet != null) {
				return false;
			}
		}
		else if (!worksheet.equals(other.worksheet)) {
			return false;
		}
		return true;
	}

}
