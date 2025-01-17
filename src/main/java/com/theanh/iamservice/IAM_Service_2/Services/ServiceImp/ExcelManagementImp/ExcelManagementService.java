package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.ExcelManagementImp;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Repositories.RepositoryImp.UserRepositoryImp;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IExcelManagement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelManagementService implements IExcelManagement {
    private static final Logger logger = Logger.getLogger(ExcelManagementService.class.getName());

    private final UserRepositoryImp userRepositoryImp;
    private final UserRepository userRepository;

    @Override
    public void importUserData(MultipartFile file) {
        if (!isValidExcelFile(file)) {
            throw new RuntimeException("Invalid file format. Please upload a valid Excel file.");
        }

        String fileName = file.getOriginalFilename();
        List<String> errorMessages = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row == null) {
                    errorMessages.add("Row " + (rowIndex + 1) + ": Row is empty.");
                    continue;
                }

                String id = validateCell(row, 0,
                        "ID", errorMessages, rowIndex, false);
                String username = validateCell(row, 1,
                        "Username", errorMessages, rowIndex, false);
                String fullName = validateCell(row, 2,
                        "Full Name", errorMessages, rowIndex, false);
                String dateOfBirth = validateCell(row, 3,
                        "Date of Birth", errorMessages, rowIndex, false);
                String street = validateCell(row, 4,
                        "Street", errorMessages, rowIndex, true);
                String ward = validateCell(row, 5,
                        "Ward", errorMessages, rowIndex, true);
                String district = validateCell(row, 6,
                        "District", errorMessages, rowIndex, true);
                String province = validateCell(row, 7,
                        "Province", errorMessages, rowIndex, true);
                String experience = validateExperienceCell(row, 8, errorMessages, rowIndex);

                if (!dateOfBirth.isEmpty()) {
                    try {
                        LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (DateTimeParseException e) {
                        errorMessages.add("Row " + (rowIndex + 1) + ": Invalid date format for Date of Birth. Expected format: yyyy-MM-dd.");
                    }
                }

                logger.info("Row " + (rowIndex) + ": Parsed data -> ID: " + id + ", Username: " + username
                        + ", Full Name: " + fullName + ", Date of Birth: " + dateOfBirth + ", Street: " + street
                        + ", Ward: " + ward + ", District: " + district + ", Province: " + province
                        + ", Experience: " + experience);
            }

            if (!errorMessages.isEmpty()) {
                errorMessages.forEach(logger::severe);
                throw new RuntimeException("Validation failed. See logs for details.");
            }

        } catch (IOException e) {
            logger.severe("Import data from file '" + fileName + "' failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String exportUserData(UserSearchRequest userSearchRequest) throws IOException {
//        List<UserEntity> userEntities = userRepository.findAll();
        List<UserEntity> userEntities = search(userSearchRequest);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("UserData");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"No.", "Username", "Full Name", "Email Address", "Date of Birth", "Address"};

            Font font = workbook.createFont();
            font.setFontName("Times New Roman");
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            int no = 1;
            for (UserEntity userEntity : userEntities) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(no++);
                row.createCell(1).setCellValue(userEntity.getUsername());
                row.createCell(2).setCellValue(userEntity.getFirstname() + " "
                        + userEntity.getLastname());
                row.createCell(3).setCellValue(userEntity.getEmailAddress());
//                row.createCell(4).setCellValue(userEntity.getDateOfBirth());
//                row.createCell(5).setCellValue(userEntity.getAddress());
            }

            for (int i = 0; i <= headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            LocalDate currentDate = LocalDate.now();
            String dateString = currentDate.toString();

            String directoryPath = "./excel_files";
            String fileName = "UserData_" + dateString + ".xlsx";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);

            try {
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                return "Excel file save to: " + file.getPath();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private List<UserEntity> search(UserSearchRequest userSearchRequest) {
        return userRepositoryImp.search(userSearchRequest);
    }

    private String validateCell(Row row, int cellIndex, String attributeName,
                                List<String> errorMessages, int rowIndex, boolean allowEmpty) {
        Cell cell = row.getCell(cellIndex);
        String value = getCellValueAsString(cell).trim();

        if (!allowEmpty && (value == null || value.isEmpty())) {
            errorMessages.add("Row " + (rowIndex + 1) + ": " + attributeName + " cannot be empty.");
        }

        return value;
    }

    private String validateExperienceCell(Row row, int cellIndex, List<String> errorMessages, int rowIndex) {
        Cell cell = row.getCell(cellIndex);
        String value = getCellValueAsString(cell).trim();

        if (value.isEmpty()) {
            errorMessages.add("Row " + (rowIndex + 1) + ": Experience cannot be empty.");
            return "";
        }

        if (cell.getCellType() != CellType.STRING) {
            errorMessages.add("Row " + (rowIndex + 1) + ": Experience must be a text value.");
        }

        if (value.length() > 50) {
            errorMessages.add("Row " + (rowIndex + 1) + ": Experience text is too long (max 50 characters).");
        }

        return value;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "Unsupported Cell Type";
        };
    }

    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return (contentType != null && (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("application/vnd.ms-excel"))) &&
                (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")));
    }
}
