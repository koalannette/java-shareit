package ru.practicum.shareit.booking.model;

public enum BookingState {
    CURRENT, PAST, FUTURE, WAITING, REJECTED, ALL, UNSUPPORTED_STATUS;

    public static BookingState checkState(String value) {
        for (BookingState state : values()) {
            if (state.toString().equals(value)) {
                return state;
            }
        }
        return UNSUPPORTED_STATUS;
    }
}
