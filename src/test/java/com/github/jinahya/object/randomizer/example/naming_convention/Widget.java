package com.github.jinahya.object.randomizer.example.naming_convention;

/**
 * A target whose randomizer no caller names.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see WidgetRandomizer
 */
public class Widget {

    @Override
    public String toString() {
        return super.toString() + '{'
               + "label=" + label
               + '}';
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    private String label;
}
