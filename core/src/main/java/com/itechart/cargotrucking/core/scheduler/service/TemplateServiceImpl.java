package com.itechart.cargotrucking.core.scheduler.service;

import com.itechart.cargotrucking.core.scheduler.exception.CreateNewFileException;
import com.itechart.cargotrucking.core.scheduler.exception.FileReaderException;
import com.itechart.cargotrucking.core.scheduler.exception.FileWriterException;
import com.itechart.cargotrucking.core.scheduler.exception.GetResourceFileException;
import com.itechart.cargotrucking.core.scheduler.exception.InvalidTemplateException;
import com.itechart.cargotrucking.core.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.compiler.STException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Value("${templates.filepath}")
    private String filepath;

    @Override
    public void saveTemplate(String template, long clientId) {
        createClientTemplateDir(clientId);

        Path path = Paths.get(filepath + '/' + clientId + "/birthday_celebration.st");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new CreateNewFileException("Cannot create file with name %s" + path.getFileName());
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(template);
        } catch (IOException e) {
            throw new FileWriterException("Couldn't write file with name %s", path.getFileName());
        }
    }

    @Override
    public String getMessage(User user) {
        StringBuilder template = new StringBuilder();
        Path path = Paths.get(filepath + '/' + user.getClientId() + "/birthday_celebration.st");

        if (!Files.exists(path)) {
            try (Scanner scanner = new Scanner(
                    new ClassPathResource("string-templates/default/birthday_celebration.st")
                            .getInputStream(),
                    "UTF-8")
            ) {
                while (scanner.hasNext()) {
                    template.append(scanner.next()).append(' ');
                }
            } catch (IOException e) {
                throw new GetResourceFileException("Couldn't get resource with name string-templates");
            }
        } else {
            try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    template.append(currentLine);
                }
            } catch (IOException e) {
                throw new FileReaderException("Couldn't read file with name %s", path.getFileName());
            }
        }

        return getRenderedMessage(template.toString(), user);
    }

    private String getRenderedMessage(String template, User user) {
        ST st;
        try {
            st = new ST(template);
        } catch (STException e) {
            throw new InvalidTemplateException("Template cannot have \\ character", e);
        }

        st.add("fullName", user.getName());
        st.add("fullName", user.getSurname());
        st.add("fullName", user.getPatronymic());

        st.add("fullAddress", user.getTown());
        st.add("fullAddress", user.getStreet());
        st.add("fullAddress", user.getHouse());
        st.add("fullAddress", user.getFlat());

        st.add("email", user.getEmail());

        return st.render();
    }

    private void createClientTemplateDir(long clientId) {
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(filepath + '/' + clientId);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
