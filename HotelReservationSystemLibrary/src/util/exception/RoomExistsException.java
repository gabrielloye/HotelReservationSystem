package util.exception;

public class RoomExistsException extends Exception {

    public RoomExistsException()
    {
    }

    public RoomExistsException(String msg)
    {
        super(msg);
    }
}
