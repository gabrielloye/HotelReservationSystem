package util.exception;

public class RoomTypeExistsException extends Exception {

    public RoomTypeExistsException()
    {
    }

    public RoomTypeExistsException(String msg)
    {
        super(msg);
    }
}
