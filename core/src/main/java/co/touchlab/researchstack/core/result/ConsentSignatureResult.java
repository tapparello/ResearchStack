package co.touchlab.researchstack.core.result;

import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSignature;

public class ConsentSignatureResult extends StepResult
{

    ConsentSignature signature;

    boolean consented;

    ConsentDocument document;

    public ConsentSignatureResult(String identifier)
    {
        super(identifier);
    }

    public ConsentSignature getSignature()
    {
        return signature;
    }

    public void setSignature(ConsentSignature signature)
    {
        this.signature = new ConsentSignature(
                signature.getTitle(),
                signature.getSignatureDateFormatString(),
                signature.getIdentifier(),
                signature.getFullName(),
                signature.getSignatureImage(),
                signature.getSignatureDate()
                );
    }

    public boolean isConsented()
    {
        return consented;
    }

    public void setConsented(boolean consented)
    {
        this.consented = consented;
    }

    public ConsentDocument getDocument()
    {
        return document;
    }

    public void setDocument(ConsentDocument document)
    {
        this.document = document;
    }
}