package blps.exceptions;

public class NoPartsAvailableException extends Exception {
  public NoPartsAvailableException(long carPartId) {
    super(String.format("No parts of id [%d] to satisfy request", carPartId));
  }
}
