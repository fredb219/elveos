//
// Copyright (c) 2011 Linkeos.
//
// This file is part of Elveos.org.
// Elveos.org is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.
//
// Elveos.org is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details.
// You should have received a copy of the GNU General Public License along
// with Elveos.org. If not, see http://www.gnu.org/licenses/.
//
package com.bloatit.model;

import java.math.BigDecimal;
import java.util.Date;

import com.bloatit.data.DaoBug.Level;
import com.bloatit.data.DaoInvoice;
import com.bloatit.framework.utils.datetime.DateUtils;
import com.bloatit.model.invoicePdf.InvoicePdfGenerator;
import com.bloatit.model.right.Action;
import com.bloatit.model.right.RgtInvoice;
import com.bloatit.model.right.UnauthorizedPrivateAccessException;

/**
 * This is a invoice.
 */
public final class Invoice extends Identifiable<DaoInvoice> {

    // /////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTION
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This class implements the method pattern, implementing the doCreate
     * method. See the base class for more informations: {@link Creator}.
     */
    private static final class MyCreator extends Creator<DaoInvoice, Invoice> {

        /*
         * (non-Javadoc)
         * @see
         * com.bloatit.model.Creator#doCreate(com.bloatit.data.DaoIdentifiable)
         */
        @SuppressWarnings("synthetic-access")
        @Override
        public Invoice doCreate(final DaoInvoice dao) {
            return new Invoice(dao);
        }
    }

    /**
     * Find a bug in the cache or create an new one.
     * 
     * @param dao the dao
     * @return null if dao is null. Else return the new invoice.
     */
    @SuppressWarnings("synthetic-access")
    public static Invoice create(final DaoInvoice dao) {
        return new MyCreator().create(dao);
    }

    /**
     * Instantiates a new invoice.
     * 
     * @param dao the dao
     */
    private Invoice(final DaoInvoice dao) {
        super(dao);
    }

    /**
     * Create a new Invoice.
     * 
     * @param member is the author of the bug.
     * @param milestone is the milestone on which this bug has been set.
     * @param title is the title of the bug.
     * @param description is a complete description of the bug.
     * @param locale is the language in which this description has been written.
     * @param errorLevel is the estimated level of the bug. see {@link Level}.
     * @throws UnauthorizedPrivateAccessException 
     */
    Invoice(final Actor<?> recipientActor, final BigDecimal totalPrice, final String deliveryName) throws UnauthorizedPrivateAccessException {
        super(generateInvoice(recipientActor, totalPrice, deliveryName));
    }

    public String getFile() throws UnauthorizedPrivateAccessException {
        tryAccess(new RgtInvoice.File(), Action.READ);
        return getDao().getFile();
    }

    public String getInvoiceNumber() throws UnauthorizedPrivateAccessException {
        tryAccess(new RgtInvoice.InvoiceNumber(), Action.READ);
        return getDao().getInvoiceNumber();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Visitor
    // /////////////////////////////////////////////////////////////////////////////////////////

    private static DaoInvoice generateInvoice(final Actor<?> recipientActor, final BigDecimal totalPrice, final String deliveryName) throws UnauthorizedPrivateAccessException {

        String invoiceType = "Elveos's fee invoice";
        
        BigDecimal internalInvoiceNumber = BigDecimal.ZERO;
        
        BigDecimal lastInternalInvoiceNumber = DaoInvoice.getLastInvoiceNumber();
        if( lastInternalInvoiceNumber != null) {
            internalInvoiceNumber = lastInternalInvoiceNumber.add(BigDecimal.ONE);
        }
        
        String invoiceId = generateInvoiceId(ModelConfiguration.getLinkeosInvoiceTemplate(), internalInvoiceNumber);
        

        String sellerName = ModelConfiguration.getLinkeosName();
        String sellerStreet = ModelConfiguration.getLinkeosStreet();
        String sellerExtras = ModelConfiguration.getLinkeosExtras();
        String sellerCity = ModelConfiguration.getLinkeosCity();
        String sellerCountry = ModelConfiguration.getLinkeosCountry();
        String sellerTaxId = ModelConfiguration.getLinkeosTaxIdentification();
        String sellerLegalId = ModelConfiguration.getLinkeosLegalIdentification();
        
        final BigDecimal taxRate = ModelConfiguration.getLinkeosTaxesRate();
        final BigDecimal priceExcludingTax = totalPrice.divide(BigDecimal.ONE.add(taxRate), BigDecimal.ROUND_HALF_EVEN);
        BigDecimal taxAmount = totalPrice.subtract(priceExcludingTax);
        
        String receiverName = recipientActor.getContact().getName();
        String receiverStreet = recipientActor.getContact().getStreet();
        String receiverExtras = recipientActor.getContact().getExtras();
        String receiverCity = recipientActor.getContact().getPostalCode() + " " + recipientActor.getContact().getCity();
        String receiverCountry = recipientActor.getContact().getCountry();
        Date invoiceDate = DateUtils.now();
        

        InvoicePdfGenerator pdfGenerator = new InvoicePdfGenerator(invoiceType,
                                                                   invoiceId,
                                                                   sellerName,
                                                                   sellerStreet,
                                                                   sellerExtras,
                                                                   sellerCity,
                                                                   sellerCountry,
                                                                   receiverName,
                                                                   receiverStreet,
                                                                   receiverExtras,
                                                                   receiverCity,
                                                                   receiverCountry,
                                                                   invoiceDate,
                                                                   deliveryName,
                                                                   priceExcludingTax,
                                                                   taxRate,
                                                                   taxAmount,
                                                                   totalPrice,
                                                                   sellerLegalId,
                                                                   sellerTaxId);

        return DaoInvoice.createAndPersist(recipientActor.getDao(),
                                           pdfGenerator.getPdfUrl(),
                                           invoiceType,
                                           invoiceId,
                                           sellerName,
                                           sellerStreet,
                                           sellerExtras,
                                           sellerCity,
                                           sellerCountry,
                                           receiverName,
                                           receiverStreet,
                                           receiverExtras,
                                           receiverCity,
                                           receiverCountry,
                                           invoiceDate,
                                           deliveryName,
                                           priceExcludingTax,
                                           taxRate,
                                           taxAmount,
                                           totalPrice,
                                           internalInvoiceNumber,
                                           sellerLegalId,
                                           sellerTaxId);
    }

    private static String generateInvoiceId(Object linkeosInvoiceTemplate, BigDecimal internalInvoiceNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <ReturnType> ReturnType accept(final ModelClassVisitor<ReturnType> visitor) {
        return visitor.visit(this);
    }

    // ///////////////////////////
    // Unprotected methods

    /**
     * This method is used only in the authentication process. You should never
     * used it anywhere else.
     * 
     * @return the actor unprotected
     * @see #getActor()
     */
    final Actor<?> getRecipientActorUnprotected() {
        return (Actor<?>) getDao().getRecipientActor().accept(new DataVisitorConstructor());
    }

}
