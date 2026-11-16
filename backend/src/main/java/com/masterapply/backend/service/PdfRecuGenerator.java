package com.masterapply.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.masterapply.backend.dto.PaiementResponse;
import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.entity.Recu;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PdfRecuGenerator {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${app.paiement.devise:TND}")
    private String devise;

    public byte[] generer(Recu recu) {
        Paiement p = recu.getPaiement();
        Candidature c = p.getCandidature();

        Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Font sousTitre = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font gras = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font mention = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        try {
            PdfWriter.getInstance(document, sortie);
            document.open();

            Paragraph entete = new Paragraph("MasterApply", titre);
            entete.setAlignment(Element.ALIGN_CENTER);
            document.add(entete);

            Paragraph objet = new Paragraph("Reçu de paiement", sousTitre);
            objet.setAlignment(Element.ALIGN_CENTER);
            objet.setSpacingAfter(25);
            document.add(objet);

            PdfPTable tableau = new PdfPTable(2);
            tableau.setWidthPercentage(100);
            tableau.setWidths(new float[] {35f, 65f});
            ligne(tableau, "Numéro du reçu", recu.getNumero(), gras, normal);
            ligne(tableau, "Date d'émission", recu.getDateGeneration().format(FORMAT), gras, normal);
            ligne(tableau, "Candidat", c.getCandidat().getPrenom() + " " + c.getCandidat().getNom(), gras, normal);
            ligne(tableau, "Email", c.getCandidat().getEmail(), gras, normal);
            ligne(tableau, "Master", c.getMaster().getIntitule(), gras, normal);
            ligne(tableau, "Campagne", c.getCampagne().getLibelle(), gras, normal);
            ligne(tableau, "Montant payé", p.getMontant().setScale(2).toPlainString() + " " + devise, gras, normal);
            ligne(tableau, "Date du paiement", p.getDatePaiement().format(FORMAT), gras, normal);
            ligne(tableau, "Référence de transaction", p.getReferenceTransaction(), gras, normal);
            ligne(tableau, "Statut du paiement", p.getStatut(), gras, normal);
            document.add(tableau);

            Paragraph avertissement = new Paragraph(PaiementResponse.MENTION.toUpperCase(), mention);
            avertissement.setAlignment(Element.ALIGN_CENTER);
            avertissement.setSpacingBefore(35);
            document.add(avertissement);

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du reçu PDF", e);
        }
        return sortie.toByteArray();
    }

    private void ligne(PdfPTable tableau, String libelle, String valeur, Font fontLibelle, Font fontValeur) {
        tableau.addCell(cellule(libelle, fontLibelle));
        tableau.addCell(cellule(valeur == null ? "" : valeur, fontValeur));
    }

    private PdfPCell cellule(String texte, Font font) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, font));
        cellule.setBorder(Rectangle.BOTTOM);
        cellule.setPadding(7);
        return cellule;
    }
}