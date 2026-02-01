# UML Legend & Architecture Reference Guide

This document explains the visual language and architectural principles used in our system diagrams to ensure a shared understanding among the team.

---

### 1. Relationship Types (Arrows and Lines)

| Symbol | Name | Description | Example |
|:-------| :--- | :--- | :--- |
| `───▷` | **Inheritance** | A solid line with a hollow triangle. Signifies an **"is-a"** relationship. | `User ───▷ BaseEntity` |
| `--▷`  | **Realization** | A dashed line with a hollow triangle. Shows a class **implements** a contract. | `SqlRepo - - ▷ UserRepository` |
| `───▶` | **Association** | A solid line with an open arrowhead. Indicates one class **uses/calls** another. | `Controller ───▶ Service` |
| `*───` | **Composition** | A solid diamond at the start. Signifies strong **ownership** (lifecycle bound). | `User *─── Email` |

---

### 2. Visibility Modifiers (Access Levels)

* **`+` Public:** Members accessible from any other class.
* **`-` Private:** Members accessible only within the class they are defined.
* **`#` Protected:** Members accessible within the class and its subclasses.

---

### 3. Architectural Stereotypes & Components

* **Package:** Represents the physical folder structure and logical layers of the project (Clean Architecture).
* **Controller (API Layer):** The entry point for external HTTP requests. It handles routing and request validation.
* **Service (Application Layer):** Orchestrates business use cases and coordinates data flow between the API and Domain layers.
* **Aggregate Root (Entity):** A core domain object with a unique identity that maintains business consistency (e.g., `User`).
* **Value Object:** Objects defined only by their attributes with no identity of their own. They are immutable (e.g., `Email`, `Name`).
* **Repository (Port):** An interface defining data access contracts, keeping the domain independent of database technologies.
* **Adapter (Infrastructure):** Concrete implementations of repositories using specific technologies like JPA, PostgreSQL, or H2.

---

### 4. Color Coding (Layer Identification)

The colors represent the layers of **Clean Architecture**:

* 🟦 **Blue (#E3F2FD):** Web / API Layer (External interfaces)
* 🟪 **Purple (#F3E5F5):** Application Layer (Orchestration logic)
* 🟨 **Yellow (#FFF9C4):** Domain Layer (Pure business logic - The Core)
* 🟩 **Green (#E8F5E9):** Infrastructure Layer (Technical details and external tools)

> **Note:** Always ensure that dependencies point **inwards** toward the Yellow (Domain) layer.