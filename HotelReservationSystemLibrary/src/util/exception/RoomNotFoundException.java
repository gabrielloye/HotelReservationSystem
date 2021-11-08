package util.exception;

public class RoomNotFoundException extends Exception {

    public RoomNotFoundException()
    {
    }

    public RoomNotFoundException(String msg)
    {
        super(msg);
    }
}
