package dataMapping.dataImport;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

/**
 * This class is able to import .xls and .xlsx files (but not from '95 or earlier it depends on four .jars
 * (poi, poi-ooxml, poi-ooxml-schemas, and dom4j) it imports the data in a simple way, so that is possible to
 * visualize the cells in an JTable until now it only imports the first sheet of a file
 *
 * @author dborck
 */
public class ImportExcelData {
	private Workbook workbook;
	private int maxColumns;
	private final Vector<String> headers = new Vector<>();
	private final Vector<Vector<String>> allData = new Vector<>();

	/**
	 * constructs the workbook of the file and extracts the data in a simple format
	 *
	 * @param file the file with the data to be mapped to the network
	 */
	public ImportExcelData(final File file) throws Exception {
		if (file.toString().endsWith(".xls")) {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			List<Record> records = null;
			try {
				records = RecordFactory.createRecords(fs.createDocumentInputStream("Workbook"));
			} catch (Exception e) {
				// it needs the String "Workbook", files created with Excel 95 or older versions do have
				// an entry which is named "Book", but "Book" does not work here because the POI only
				// supports Excel files 97+
				Iterator<Entry> entries = fs.getRoot().getEntries();
				while (entries.hasNext()) {
					String entryName = entries.next().getName();
					if (entryName.equals("Book")) {
						throw new ExcelException("old Excel file");
					}
				}
			}

			DimensionsRecord dr = null;
			if (records != null) {
				for (Record r : records) {
					if (r instanceof DimensionsRecord) {
						dr = (DimensionsRecord) r;
						break;
					}
				}
			}
			if (dr == null) {
				throw new RuntimeException("Cannot find dimension of excel sheet");
			}
			maxColumns = dr.getLastCol();
		}
		// creates the workbook of the file depending on the file type xls or xlsx
		try {
			this.workbook = WorkbookFactory.create(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL); // empty rows are recognized
		// selects the first sheet
		// TODO: maybe let the user select another sheet
		Sheet sheet = workbook.getSheetAt(0);
		// get the maxColumns of an xlsx sheet
		if (file.toString().endsWith(".xlsx")) {
			XSSFSheet sheetx = (XSSFSheet) sheet;
			CTSheetDimension dimension = sheetx.getCTWorksheet().getDimension();
			String sheetDimensions = dimension.getRef();
			String dimTo = sheetDimensions.split(":")[1].split("\\d")[0];
			maxColumns = 0;
			for (int i = 0; i < dimTo.length(); i++) {
				maxColumns = maxColumns + Character.getNumericValue(dimTo.charAt(i)) - 9;
			}
		}
		// TODO: maybe let the user select the header row and change it here
		final Row headerRow = sheet.getRow(0);
		final DataFormatter fmt = new DataFormatter(Locale.US);
		for (int i = 0; i < maxColumns; i++) {
			final Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if (cell == null) {
				headers.add("\n");
			} else {
				headers.add(fmt.formatCellValue(cell));
			}
		}
		for (int rn = 1; rn <= sheet.getLastRowNum(); rn++) {
			final Vector<String> d = new Vector<>();
			final Row row = sheet.getRow(rn);
			if (row == null) {
				d.add("\n");
			} else {
				for (int cn = 0; cn < row.getLastCellNum(); cn++) {
					Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell == null) {
						d.add("\n");
					} else {
						d.add(fmt.formatCellValue(cell));
					}
				}
			}
			allData.add(d);
		}
	}

	public Vector<Vector<String>> getDataVector() {
		return allData;
	}

	public Vector<String> getHeaderVector() {
		return headers;
	}
}
