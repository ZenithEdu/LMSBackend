package com.MiniLms.LMSBackend.utils;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Gender;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Role;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelFileParser {
    public static List<StudentRegistrationRequestDTO> parseExcelFile(MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("Excel file must not be null or empty");
        }
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            List<StudentRegistrationRequestDTO> students = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            for(Row row : sheet){
                if(row.getRowNum() == 0)continue;

                StudentRegistrationRequestDTO student = StudentRegistrationRequestDTO.builder()
                    .uniId(getCellValue(row,0,dataFormatter))
                    .name(getCellValue(row,1,dataFormatter))
                    .email(getCellValue(row,2,dataFormatter))
                    .branch(getCellValue(row,3,dataFormatter))
                    .role(Role.STUDENT)
                    .build();

                students.add(student);
            }
            return students;
        }catch (Exception e){
            throw new IOException("Failed to parse Excel File: " + e.getMessage(),e);
        }
    }
    private static String getCellValue(Row row, int cellIndex,DataFormatter dataFormatter){
        Cell cell = row.getCell(cellIndex,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? null : dataFormatter.formatCellValue(cell).trim();
    }
    private static Gender setStudentGender(String gender){
        if(gender.equalsIgnoreCase("male")){
            return Gender.MALE;
        }else if(gender.equalsIgnoreCase("female")){
            return Gender.FEMALE;
        }else{
            return Gender.OTHER;
        }
    }
}
