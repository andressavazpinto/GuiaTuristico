package model;

public class Translate {
    private String q, source, target, format;

    public Translate() {
    }

    public Translate(String q, String source, String target, String format) {
        this.q = q;
        this.source = source;
        this.target = target;
        this.format = format;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "Translate{" +
                "q='" + q + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
