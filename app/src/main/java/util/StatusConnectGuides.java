package util;

public enum StatusConnectGuides {
    /*Regra deste status (depende dos status de search dos usuários)
        Found         -> Found ^ Found
        WaitingAnswer -> WaitingAnswer v Found; WaitingAnswer v Accepted
        Accepted      -> Accepted ^ Accepted
        Rejected      -> Rejected v Found; Rejected v WaitingAnswer
        */
    Found, WaitingAnswer, Accepted, Rejected;
}
