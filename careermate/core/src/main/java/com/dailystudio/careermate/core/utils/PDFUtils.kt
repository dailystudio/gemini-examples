package com.dailystudio.careermate.core.utils

import android.net.Uri
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.IOException
import java.io.InputStream

object PDFUtils {

    fun extractTextFromPdf(pdfPath: String): String? {
        PDFBoxResourceLoader.init(GlobalContextWrapper.context)

        // Using the `PdfDocument` class
        var stream: InputStream? = null
        var pdfDocument: PDDocument? = null

        return try {
            stream =
                GlobalContextWrapper.context?.contentResolver?.openInputStream(
                    Uri.parse(pdfPath)
                )

            stream?.let {
                pdfDocument = PDDocument.load(it)
                val pdfStripper = PDFTextStripper()

                pdfStripper.getText(pdfDocument)
            }
        } catch (e: IOException) {
            Logger.error("failed to extract text from pdf [${pdfPath}]: $e")
            null
        } finally {
            stream?.close()
        }
    }
}