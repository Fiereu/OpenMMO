package de.fiereu.openmmo.protocols.tls.packets.builders

import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacket
import java.time.LocalDateTime

class LoginResponsePacketBuilder {
  companion object {
    fun withState(state: LoginState): LoginResponsePacket {
      require(state != LoginState.RATE_LIMITED && state != LoginState.RATE_LIMITED_2FA) {
        "Use withRateLimit() for rate limited states"
      }
      return LoginResponsePacket(state)
    }

    fun withRateLimit(state: LoginState, ratelimitEnd: LocalDateTime): LoginResponsePacket {
      require(state == LoginState.RATE_LIMITED || state == LoginState.RATE_LIMITED_2FA) {
        "Rate limit end time can only be used with RATE_LIMITED states"
      }
      return LoginResponsePacket(state, ratelimitEnd)
    }
  }
}
