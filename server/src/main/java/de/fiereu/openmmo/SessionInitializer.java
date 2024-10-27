package de.fiereu.openmmo;

@FunctionalInterface
public interface SessionInitializer {
  Session initializeSession(Session.Side side);
}
