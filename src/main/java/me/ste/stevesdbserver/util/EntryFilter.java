package me.ste.stevesdbserver.util;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class EntryFilter {
    private final ComparatorOperation operation;
    private final String comparedValue;

    public EntryFilter(ComparatorOperation operation, String comparedValue) {
        this.operation = operation;
        this.comparedValue = comparedValue;
    }

    public boolean matches(Object value) {
        String str = String.valueOf(value);
        if(this.operation == ComparatorOperation.EQUAL_TO) {
            return str.equals(this.comparedValue);
        } else if(this.operation == ComparatorOperation.NOT_EQUAL_TO) {
            return !str.equals(this.comparedValue);
        } else if(this.operation == ComparatorOperation.EQUAL_TO_IGNORE_CASE) {
            return str.equalsIgnoreCase(this.comparedValue);
        } else if(this.operation == ComparatorOperation.NOT_EQUAL_TO_IGNORE_CASE) {
            return !str.equalsIgnoreCase(this.comparedValue);
        } else if(this.operation == ComparatorOperation.GREATER_THAN) {
            try {
                int a = Integer.parseInt(str);
                int b = Integer.parseInt(this.comparedValue);
                return a > b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LESS_THAN) {
            try {
                int a = Integer.parseInt(str);
                int b = Integer.parseInt(this.comparedValue);
                return a < b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.GREATER_THAN_OR_EQUAL_TO) {
            try {
                int a = Integer.parseInt(str);
                int b = Integer.parseInt(this.comparedValue);
                return a >= b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LESS_THAN_OR_EQUAL_TO) {
            try {
                int a = Integer.parseInt(str);
                int b = Integer.parseInt(this.comparedValue);
                return a <= b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.STARTS_WITH) {
            return str.startsWith(this.comparedValue);
        } else if(this.operation == ComparatorOperation.ENDS_WITH) {
            return str.endsWith(this.comparedValue);
        } else if(this.operation == ComparatorOperation.STARTS_WITH_IGNORE_CASE) {
            return str.toLowerCase().startsWith(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.ENDS_WITH_IGNORE_CASE) {
            return str.toLowerCase().endsWith(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.DOES_NOT_START_WITH) {
            return !str.startsWith(this.comparedValue);
        } else if(this.operation == ComparatorOperation.DOES_NOT_END_WITH) {
            return !str.endsWith(this.comparedValue);
        } else if(this.operation == ComparatorOperation.DOES_NOT_START_WITH_IGNORE_CASE) {
            return !str.toLowerCase().startsWith(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.DOES_NOT_END_WITH_IGNORE_CASE) {
            return !str.toLowerCase().endsWith(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.CONTAINS) {
            return str.contains(this.comparedValue);
        } else if(this.operation == ComparatorOperation.CONTAINS_IGNORE_CASE) {
            return str.toLowerCase().contains(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.DOES_NOT_CONTAIN) {
            return !str.contains(this.comparedValue);
        } else if(this.operation == ComparatorOperation.DOES_NOT_CONTAIN_IGNORE_CASE) {
            return !str.toLowerCase().contains(this.comparedValue.toLowerCase());
        } else if(this.operation == ComparatorOperation.MATCHES_REGEXP) {
            return Pattern.compile(this.comparedValue).matcher(str).matches();
        } else if(this.operation == ComparatorOperation.DOES_NOT_MATCH_REGEXP) {
            return !Pattern.compile(this.comparedValue).matcher(str).matches();
        } else if(this.operation == ComparatorOperation.LENGTH_EQUAL_TO) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() == b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LENGTH_NOT_EQUAL_TO) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() != b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LENGTH_GREATER_THAN) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() > b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LENGTH_LESS_THAN) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() < b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LENGTH_GREATER_THAN_OR_EQUAL_NO) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() >= b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else if(this.operation == ComparatorOperation.LENGTH_LESS_THAN_OR_EQUAL_TO) {
            try {
                int b = Integer.parseInt(this.comparedValue);
                return str.length() <= b;
            } catch(NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean entryMatchesFilters(Map<Integer, Object> entry, Map<Integer, EntryFilter> filters) {
        for(Map.Entry<Integer, Object> values : entry.entrySet()) {
            if(filters.containsKey(values.getKey()) && !filters.get(values.getKey()).matches(values.getValue())) {
                return false;
            }
        }
        return true;
    }

    public static Map<Integer, Map<Integer, Object>> removeUnmatchedEntries(Map<Integer, Map<Integer, Object>> entries, Map<Integer, EntryFilter> filters) {
        entries.entrySet().removeIf(next -> !EntryFilter.entryMatchesFilters(next.getValue(), filters));
        return entries;
    }
}