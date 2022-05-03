package team.proiectextins.domain;

public enum Status {
    APPROVED("approved"), REJECTED("rejected"), PENDING("pending");
    public String s;

    Status(String s) {
        this.s = s;
    }
}

