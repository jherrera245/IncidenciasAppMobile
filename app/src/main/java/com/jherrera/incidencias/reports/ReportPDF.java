package com.jherrera.incidencias.reports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.jherrera.incidencias.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReportPDF {
    private JSONArray jsonArrayIncidencias;
    private JSONObject jsonObjectIncidencia;
    private Context context;

    public ReportPDF(JSONArray jsonArrayIncidencias, Context context) {
        this.jsonArrayIncidencias = jsonArrayIncidencias;
        this.context = context;
        this.jsonObjectIncidencia = null;
    }

    public ReportPDF(JSONObject jsonObjectIncidencia, Context context) {
        this.jsonObjectIncidencia = jsonObjectIncidencia;
        this.context = context;
        this.jsonArrayIncidencias = null;
    }

    public void createPDFAll() throws FileNotFoundException {
        if (jsonArrayIncidencias != null) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            String randomName = getAlphaNumericString(15);
            File file = new File(path, "Reporte-incidencia-"+randomName+".pdf");
            OutputStream outputStream = new FileOutputStream(file);

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            Image image = reportImage();
            document.add(image);

            Paragraph title = titleReport();
            document.add(title);

            float[] columnWidth = {150, 100, 220, 50, 90};
            Table table = new Table(columnWidth);
            //definiendo header de la tabla
            table.addCell(cellTableHeader("Empleado", 12));
            table.addCell(cellTableHeader("Tipo", 12));
            table.addCell(cellTableHeader("Descripción", 12));
            table.addCell(cellTableHeader("Estado", 12));
            table.addCell(cellTableHeader("Fecha", 12));

            try{
                boolean aux = true;
                for (int i = 0; i < jsonArrayIncidencias.length(); i++) {
                    JSONObject incidencia = jsonArrayIncidencias.getJSONObject(i);
                    table.addCell(cellTableBody(
                            incidencia.getString("nombres")+ " "+incidencia.getString("apellidos"), 10, aux
                    ));
                    table.addCell(cellTableBody(incidencia.getString("tipo"), 10, aux));
                    table.addCell(cellTableBody(incidencia.getString("descripcion"), 10, aux));

                    int resolucion = Integer.parseInt(incidencia.getString("resolucion"));
                    String estado = getEstadoResolucion(resolucion);

                    table.addCell(cellTableBody(estado, 10, aux));
                    table.addCell(cellTableBody(incidencia.getString("fecha"), 10, aux));
                    aux = !aux;
                }
            }catch (JSONException e) {
                Toast.makeText(context, "Error al generar filas", Toast.LENGTH_SHORT).show();
            }

            document.add(new Paragraph("\n"));
            document.add(table);
            document.close();

            Toast.makeText(
                    context,
                    "Se creo el reporte correctamente!!! ver en "+Environment.DIRECTORY_DOCUMENTS,
                    Toast.LENGTH_LONG
            ).show();
        }else {
            Toast.makeText(context, "No se puede generar el pdf con este metodo", Toast.LENGTH_SHORT).show();
        }
    }

    public void createPDFById() throws  FileNotFoundException {
        if (jsonObjectIncidencia != null) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            String randomName = getAlphaNumericString(15);
            File file = new File(path, "Reporte-incidencia-"+randomName+".pdf");
            OutputStream outputStream = new FileOutputStream(file);

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            Image image = reportImage();
            document.add(image);

            Paragraph title = titleReport();
            document.add(title);

            try{
                JSONObject incidencia = jsonObjectIncidencia.getJSONObject("incidencia");
                document.add(titulo("Detalles del Empleado"));

                float[] columnWidth = {600};
                Table tableInfoEmpleado = new Table(columnWidth);
                tableInfoEmpleado.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                tableInfoEmpleado.addCell(new Cell().add(titulo("Reportado por")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(incidencia.getString("nombres") + " "+ incidencia.getString("apellidos"))));
                tableInfoEmpleado.addCell(new Cell(). add(titulo("Departamento")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(incidencia.getString("departamento"))));
                tableInfoEmpleado.addCell(new Cell().add(titulo("Cargo")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(incidencia.getString("cargo"))));
                tableInfoEmpleado.addCell(new Cell().add(titulo("Tipo de Incidencia")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(incidencia.getString("tipo"))));
                tableInfoEmpleado.addCell(new Cell().add(titulo("Descripción del problema")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(incidencia.getString("descripcion"))));

                int resolucion = Integer.parseInt(incidencia.getString("resolucion"));
                String estado = getEstadoResolucion(resolucion);

                tableInfoEmpleado.addCell(new Cell().add(titulo("Estado de incidencia")));
                tableInfoEmpleado.addCell(new Cell().add(parrafo(estado)));
                document.add(tableInfoEmpleado);

                if (!jsonObjectIncidencia.isNull("retroalimentacion")) {
                    JSONObject retroalimentacion = jsonObjectIncidencia.getJSONObject("retroalimentacion");

                    Table tableInfoAdmin = new Table(columnWidth);
                    tableInfoAdmin.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                    tableInfoAdmin.addCell(new Cell().add(titulo("Revisado por")));
                    tableInfoAdmin.addCell(new Cell().add(parrafo(retroalimentacion.getString("nombres") + " "+ retroalimentacion.getString("apellidos"))));
                    tableInfoAdmin.addCell(new Cell(). add(titulo("Departamento")));
                    tableInfoAdmin.addCell(new Cell().add(parrafo(retroalimentacion.getString("departamento"))));
                    tableInfoAdmin.addCell(new Cell().add(titulo("Cargo")));
                    tableInfoAdmin.addCell(new Cell().add(parrafo(retroalimentacion.getString("cargo"))));
                    tableInfoAdmin.addCell(new Cell().add(titulo("Retroalimentación del problema")));
                    tableInfoAdmin.addCell(new Cell().add(parrafo(retroalimentacion.getString("retroalimentacion"))));

                    document.add(new Paragraph("\n"));
                    document.add(titulo("Detalles de la retroalimentación"));
                    document.add(tableInfoAdmin);
                }else {
                    document.add(new Paragraph("Esta incidencia no ha sido revisada").setBold().setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.RED));
                }

            }catch (JSONException e) {
                Toast.makeText(context, "No se puede recuperara la información", Toast.LENGTH_SHORT).show();
            }

            document.add(new Paragraph("\n"));
            document.close();

            Toast.makeText(
                    context,
                    "Se creo el reporte correctamente!!! ver en "+Environment.DIRECTORY_DOCUMENTS,
                    Toast.LENGTH_LONG
            ).show();
        }else {
            Toast.makeText(context, "No se puede crear el pdf con este metodo", Toast.LENGTH_SHORT).show();
        }
    }

    private Paragraph parrafo(String text) {
        return new Paragraph(text).setTextAlignment(TextAlignment.JUSTIFIED).setFontSize(12);
    }

    private Paragraph titulo(String text) {
        return new Paragraph(text).setFontSize(14).setFontColor(ColorConstants.DARK_GRAY).setBold();
    }

    private String getAlphaNumericString(int size) {
        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }


    private String getEstadoResolucion(int resolucion) {
        switch (resolucion) {
            case 0:
                return  "En revisión";
            case 1:
                return  "Acción Correctiva";
            case 2:
                return "Acción Preventiva";
            case 3:
                return  "Sin solución";
            default:
                return "No aplica";
        }
    }

    private Paragraph titleReport() {
        Paragraph paragraph = new Paragraph("Reporte de incidencias");
        paragraph.setFontSize(20);
        paragraph.setBold();
        paragraph.setTextAlignment(TextAlignment.CENTER);

        return paragraph;
    }

    private Cell cellTableHeader(String text, int fontSize) {
        return new Cell().add(
                new Paragraph(text).setFontSize(fontSize).setBold().setTextAlignment(TextAlignment.CENTER)
        );
    }

    private Cell cellTableBody(String text, int fontSize, boolean isChange) {
        return new Cell().add(
                new Paragraph(text).setFontSize(fontSize)
        ).setBackgroundColor(isChange ? ColorConstants.LIGHT_GRAY : ColorConstants.WHITE);
    }

    //este metodo crea una imagen que se agrega al reporte
    private Image reportImage() {
        Drawable drawable = context.getDrawable(R.drawable.img_report);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);
        image.setHeight(100);
        image.setWidth(100);
        return  image;
    }
}
