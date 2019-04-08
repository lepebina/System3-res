package br.com.softbox.core;


public class AtestLog {
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private final String _me;

    public AtestLog(String me) {
        _me = me;
    }

    public void trace(String msg) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.TRACE, _me, msg);
        _evidence.log(logEvent);
    }
    public void info(String msg) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO, _me, msg);
        _evidence.log(logEvent);
    }
    public void oops(String msg) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.OOPS, _me, msg);
        _evidence.log(logEvent);
    }

    public void step(String stepName) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO, _me + " STEP", stepName);
        _evidence.log(logEvent);
    }

    public void scenario(String scenario) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO, _me + " SCENARIO", scenario);
        _evidence.log(logEvent);

    }

    public void status(String msg) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO, _me, msg);
        _evidence.log(logEvent);
    }

    public String me() {
        return _me;
    }
}
