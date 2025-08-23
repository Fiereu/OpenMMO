package buildsrc.common

val KEYS_RESOURCE_LOCATION = "src/main/resources"

tasks.register<Copy>("copyPublicKeyChat") {
  group = "openmmo"
  description = "Copy chat keys from \":keys\" to the current projects resources directory"
  from(project(":keys").tasks.getByName("generateChat").outputs.files) {
    include("*.public.pem")
  }
  into(project.layout.projectDirectory.dir(KEYS_RESOURCE_LOCATION))
}

tasks.register<Copy>("copyPrivateKeyChat") {
  group = "openmmo"
  description = "Copy chat keys from \":keys\" to the current projects resources directory"
  from(project(":keys").tasks.getByName("generateChat").outputs.files) {
    include("*.public.pem")
  }
  into(project.layout.projectDirectory.dir(KEYS_RESOURCE_LOCATION))
}

tasks.register("copyKeysChat") {
  group = "openmmo"
  description = "Copies all chat keys from \":keys\" to the current projects resources directory"
  dependsOn("copyPublicKeyChat", "copyPrivateKeyChat")
}

tasks.register<Copy>("copyPublicKeyGame") {
  group = "openmmo"
  description = "Copy game keys from \":keys\" to the current projects resources directory"
  from(project(":keys").tasks.getByName("generateGame").outputs.files) {
    include("*.public.pem")
  }
  into(project.layout.projectDirectory.dir(KEYS_RESOURCE_LOCATION))
}

tasks.register<Copy>("copyPrivateKeyGame") {
  group = "openmmo"
  description = "Copy game keys from \":keys\" to the current projects resources directory"
  from(project(":keys").tasks.getByName("generateGame").outputs.files) {
    include("*.private.pem")
  }
  into(project.layout.projectDirectory.dir(KEYS_RESOURCE_LOCATION))
}

tasks.register("copyKeysGame") {
  group = "openmmo"
  description = "Copies all game keys from \":keys\" to the current projects resources directory"
  dependsOn("copyPublicKeyGame", "copyPrivateKeyGame")
}

tasks.register("copyPublicKeys") {
  group = "openmmo"
  description = "Copies all public keys from \":keys\" to the current projects resources directory"
  dependsOn("copyPublicKeyChat", "copyPublicKeyGame")
}

tasks.register("copyKeys") {
  group = "openmmo"
  description = "Copies all keys from \":keys\" to the current projects resources directory"
  dependsOn("copyKeysChat", "copyKeysGame")
}