package br.com.softbox.core;

import br.com.softbox.utils.Utils;

import java.util.HashMap;


public class AtestLogEvent {
    public enum LogType {INFO, OOPS, TRACE};

    private String _timestamp;
    private LogType _logType;
    private String _publisher;
    private String _msg;
    private HashMap<String, String> _dump;

    public AtestLogEvent(LogType type, String publisher, String msg) {
        _logType = type;
        _publisher = publisher;
        _msg = msg;
        _timestamp = Utils.getTimeStamp();
    }

    public AtestLogEvent(LogType type, String publisher, String msg, HashMap<String, String> dump) {
        _logType = type;
        _publisher = publisher;
        _msg = msg;
        _dump = dump;
        _timestamp = Utils.getTimeStamp();
    }

    public String getPublisher() {
        return _publisher;
    }

    public HashMap<String, String> getDump() {
        return _dump;
    }

    public String getMsg() {
        return _msg;
    }

    public LogType getLogType() {
        return _logType;
    }

    public String getTimestamp() {
        return _timestamp;
    }

}
