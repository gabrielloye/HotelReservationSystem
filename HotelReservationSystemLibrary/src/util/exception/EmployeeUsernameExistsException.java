package util.exception;

public class EmployeeUsernameExistsException extends Exception {

    public EmployeeUsernameExistsException()
    {
    }

    public EmployeeUsernameExistsException(String msg)
    {
        super(msg);
    }
}
