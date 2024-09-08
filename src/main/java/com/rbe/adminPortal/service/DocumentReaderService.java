package com.rbe.adminPortal.service;

import com.rbe.adminPortal.dto.Element;
import com.rbe.adminPortal.dto.QuestionObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentReaderService {

    public List<QuestionObject> readFile(@RequestParam("file") MultipartFile file) {
        List<QuestionObject> elementList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            boolean isFirst = true;
            StringBuilder text = new StringBuilder();
            Pattern quesPattern = Pattern.compile("^Q\\d+\\.");
            String elementSelector = "ques";
            String currentElement = null;
            List<Blob> images = new ArrayList<>();
            QuestionObject qo = new QuestionObject();
            Element e;
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                XmlCursor cursor = paragraph.getCTP().newCursor();
                cursor.selectPath("./*");

                while (cursor.toNextSelection()) {
                    XmlObject obj = cursor.getObject();
                    if (obj instanceof CTOMathPara) {
                        CTOMathPara mathPara = (CTOMathPara) obj;
                        for (CTOMath math : mathPara.getOMathList()) {
                            text.append(cursor.getTextValue()).append("\n");
                        }
                    } else if (obj instanceof CTOMath) {
                        CTOMath math = (CTOMath) obj;
                        text.append(cursor.getTextValue()).append("\n");
                    } else {
                        // Handle regular text
                        if (obj instanceof org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR) {
                            text.append(cursor.getTextValue());
                        }
                    }
                }
                String line = paragraph.getText();
                Matcher quesMatcher = quesPattern.matcher(line);
                if (quesMatcher.lookingAt()) {
                    currentElement = "ques";
                } else if (line.startsWith("(a)")) {
                    currentElement = "ans1";
                } else if (line.startsWith("(b)")) {
                    currentElement = "ans2";
                } else if (line.startsWith("(c)")) {
                    currentElement = "ans3";
                } else if (line.startsWith("(d)")) {
                    currentElement = "ans4";
                }else if (line.startsWith("Ans")) {
                    currentElement = "Ans";
                }else if (line.startsWith("Solution")) {
                    currentElement = "Solution";
                }
                if (!elementSelector.equals(currentElement)) {
                    e = new Element();
                    e.setText(text.toString());
                    e.setImage(images);
                    switch (elementSelector) {
                        case "ques" -> qo.setQuestion(e);
                        case "ans1" -> qo.setOpt1(e);
                        case "ans2" -> qo.setOpt2(e);
                        case "ans3" -> qo.setOpt3(e);
                        case "ans4" -> qo.setOpt4(e);
                        case "Ans" -> qo.setAns(text.toString());
                        case "Solution" -> qo.setSol(e);
                    }
                    elementSelector = currentElement;
                    text.setLength(0);
                    images = new ArrayList<>();
                    if (currentElement.equals("ques") && !isFirst) {
                        elementList.add(qo);
                        qo = new QuestionObject();
                    }
                    isFirst = false;
                }
                text.append(paragraph.getText());

                for (XWPFRun run : paragraph.getRuns()) {
                    List<XWPFPicture> picture = run.getEmbeddedPictures();
                    if (!picture.isEmpty()) {
                        images.add(getBlob(picture.get(0)));
                    }
                }

                text.append("\n");


            }
            e = new Element();
            e.setText(text.toString());
            e.setImage(images);
            qo.setSol(e);
            elementList.add(qo);
            return elementList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Blob getBlob(XWPFPicture picture) {
        try {
            byte[] imageData = picture.getPictureData().getData();
            return new SerialBlob(imageData);
        } catch (Exception e) {
            return null;
        }
    }
}
