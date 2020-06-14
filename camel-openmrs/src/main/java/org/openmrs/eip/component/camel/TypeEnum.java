package org.openmrs.eip.component.camel;

public enum TypeEnum {
    FILE("<FILE>", "</FILE>");

    private String openingTag;
    private String closingTag;

    TypeEnum(final String openingTag, final String closingTag) {
        this.openingTag = openingTag;
        this.closingTag = closingTag;
    }

    public String getOpeningTag() {
        return openingTag;
    }

    public String getClosingTag() {
        return closingTag;
    }
}
