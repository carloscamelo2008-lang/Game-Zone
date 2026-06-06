#  GameZone – Sistema de Gestión de Videojuegos

**Universidad Popular del César – Programación de Computadores II (SS300)**

---

##  Integrantes
- Integrante 1: `[CARLOS CAMELO]`
- Integrante 2: `[ESTEBAN MEJIA]`

---

##  Estructura del Proyecto

```
GameZone/
├── src/main/java/
│   ├── module-info.java
│   ├── gamezone/
│   │   ├── Main.java                          ← Punto de entrada
│   │   ├── entities/
│   │   │   ├── VideoGame.java                 ← Clase abstracta
│   │   │   ├── DigitalVideoGame.java           ← Videojuego digital
│   │   │   ├── PhysicalVideoGame.java          ← Videojuego físico
│   │   │   └── Sale.java                      ← Registro de venta
│   │   ├── repositories/
│   │   │   ├── VideoGameRepository.java        ← CRUD + JSON
│   │   │   └── SaleRepository.java            ← Registro en memoria
│   │   ├── services/
│   │   │   ├── Sellable.java                  ← Interface
│   │   │   ├── Displayable.java               ← Interface
│   │   │   └── GameService.java               ← Lógica de negocio
│   │   └── ui/
│   │       ├── MainMenu.java                  ← Ventana principal JavaFX
│   │       ├── AddGamePanel.java              ← CRUD videojuegos
│   │       ├── ListGamesPanel.java            ← Listar catálogo
│   │       ├── SearchByTitlePanel.java        ← Búsqueda por título
│   │       ├── SearchByPlatformPanel.java     ← Búsqueda por plataforma
│   │       ├── SellGamePanel.java             ← Realizar venta
│   │       └── SalesHistoryPanel.java         ← Historial de ventas
├── data/
│   └── videogames.json                        ← Persistencia JSON
└── pom.xml
```

---

## Arquitectura en Capas

| Capa          | Paquete              | Responsabilidad                            |
|---------------|----------------------|--------------------------------------------|
| **Entities**  | `gamezone.entities`  | Modelo de datos (POO, herencia, abstractas)|
| **Repository**| `gamezone.repositories` | Persistencia CRUD en JSON              |
| **Services**  | `gamezone.services`  | Lógica de negocio, interfaces, excepciones |
| **UI**        | `gamezone.ui`        | Interfaz JavaFX, manejo de alertas         |

---

##  Cómo Ejecutar

### Con Maven
```bash
mvn javafx:run
```

### Compilar y empaquetar
```bash
mvn clean package
java -jar target/GameZone-1.0.0.jar
```

---

## ✅ Criterios Cumplidos

- [x] Herencia: `DigitalVideoGame` y `PhysicalVideoGame` extienden `VideoGame`
- [x] Clase abstracta con `calculateFinalPrice()` abstracto
- [x] Interfaces `Sellable` y `Displayable` en capa services
- [x] CRUD completo con persistencia en JSON
- [x] Validaciones con excepciones (`IllegalArgumentException`, `ArithmeticException`)
- [x] Alertas JavaFX (`Alert.AlertType`) para feedback al usuario
- [x] Arquitectura en capas (entities / repositories / services / ui)
- [x] Interfaz UI con menú interactivo (JavaFX)
- [x] Código limpio con convenciones Java (camelCase, PascalCase)

