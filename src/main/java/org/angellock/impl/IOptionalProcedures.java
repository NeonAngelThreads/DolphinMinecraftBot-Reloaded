package org.angellock.impl;

public interface IOptionalProcedures {
    void onJoin();
    void onQuit(String reason);
    void onKicked();
    void onPreLogin();
}
