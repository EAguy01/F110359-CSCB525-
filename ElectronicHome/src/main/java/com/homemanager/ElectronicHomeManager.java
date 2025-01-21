package com.homemanager;

import com.homemanager.model.*;
import com.homemanager.service.*;
import com.homemanager.util.ValidationException;
import com.homemanager.model.Payment;
import com.homemanager.service.PaymentService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


//controller
public class ElectronicHomeManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CompanyService companyService = new CompanyService();
    private static final BuildingService buildingService = new BuildingService();
    private static final EmployeeService employeeService = new EmployeeService();
    private static final ApartmentService apartmentService = new ApartmentService();
    private static final ResidentService residentService = new ResidentService();
    private static final FeeService feeService = new FeeService();
    private static final PaymentService paymentService = new PaymentService();
    private static final ApartmentOwnershipService ApartmentOwnershipService = new ApartmentOwnershipService(); // Add this service



    public static void main(String[] args) {
        runManager();
    }

    // cikul koito vurti prilojenieto pokazvaiki meniuto i upravlqva interakciite na user-a
    private static void runManager() {
        boolean running = true; // programata stoi dokato user-a ne izlezne
        while (running) {
            displayMainMenu();  // pokazva menuto
            int choice = getIntegerInput(); // vzima izbora na klienta
            switch (choice) {  // sledvat opciite koito mogat da se izberat
                case 1: handleCompanyManagement(); break;
                case 2: handleBuildingManagement(); break;
                case 3: handleEmployeeManagement(); break;
                case 4: handleApartmentManagement(); break;
                case 5: handleResidentManagement(); break;
                case 6: handleFeeManagement(); break;
                case 7: handlePaymentManagement(); break;
                case 8: ApartmentOwnershipManager.handleApartmentOwnershipManagement(); break;

                // case 8: handleReports(); break; // ne rabotesht izbor
                //sledvat izbor da bude zatvreno menute i invalidni inputs
                case 0: running = false; System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    //izisva validen inout ot potrebitelq dokato ne ne pravilno chisloto
    private static int getIntegerInput() {
        while (!scanner.hasNextInt()) { //checkva dali e chislo
            scanner.next(); //chiosti greshniq input i otdolu piuta custoemra pak
            System.out.println("Invalid input. Please enter a number.");
        }
        int number = scanner.nextInt(); // tuk chete pravilnoto cvhislo otdolu chisti buffera i vrushta chisloto
        scanner.nextLine();
        return number;
    }
    //printirame izborite na klienta na display pokazvaiki vsichki opcii
    private static void displayMainMenu() {
        System.out.println("\n=== Electronic Home Manager ===");
        System.out.println("1. Company Management");
        System.out.println("2. Building Management");
        System.out.println("3. Employee Management");
        System.out.println("4. Apartment Management");
        System.out.println("5. Resident Management");
        System.out.println("6. Fee Management");
        System.out.println("7. Payment Management");
        System.out.println("8. Apartment Ownership Management");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }




    //glavno menu za kompaniite i opciite koito imame za tqh
    private static void handleCompanyManagement() {
        while (true) {
            System.out.println("\n=== Company Management ===");
            System.out.println("1. Create new company");
            System.out.println("2. View all companies");
            System.out.println("3. View company details");
            System.out.println("4. Update company");
            System.out.println("5. Delete company");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntegerInput();
            switch (choice) {
                case 1: createNewCompany(); break;
                case 2: viewAllCompanies(); break;
                case 3: viewCompanyDetails(); break;
                case 4: updateCompany(); break;
                case 5: deleteCompany(); break;
                case 0: return;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // mini menu za suzdavane na kompaniq
    private static void createNewCompany() {
        System.out.println("\n=== Create New Company ===");
        System.out.print("Enter company name: ");
        String name = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        Company company = new Company(name, address, contactNumber, email);
        try {
            companyService.createCompany(company);
            System.out.println("Company created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu  za vijdane na vischki kompanii
    private static void viewAllCompanies() {
        try {
            List<Company> companies = companyService.getAllCompanies();
            if (companies.isEmpty()) {
                System.out.println("No companies found.");
                return;
            }
            System.out.println("\nList of Companies:");
            System.out.println("------------------");
            for (Company company : companies) {
                System.out.printf("ID: %d, Name: %s, Contact: %s%n",
                        company.getCompanyId(),
                        company.getCompanyName(),
                        company.getContactNumber());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving companies: " + e.getMessage());
        }
    }
    // mini menu za vijdane na detailite na kompaniq
    private static void viewCompanyDetails() {
        System.out.print("Enter company ID: ");
        int companyId = getIntegerInput();
        try {
            Company company = companyService.getCompanyById(companyId);
            if (company == null) {
                System.out.println("Company not found.");
                return;
            }
            System.out.println("\nCompany Details:");
            System.out.println("----------------");
            System.out.println("Name: " + company.getCompanyName());
            System.out.println("Address: " + company.getAddress());
            System.out.println("Contact Number: " + company.getContactNumber());
            System.out.println("Email: " + company.getEmail());
        } catch (SQLException e) {
            System.out.println("Error retrieving company details: " + e.getMessage());
        }
    }
    // mini menu za redaktirane na kompaniq
    private static void updateCompany() {
        System.out.print("Enter company ID: ");
        int companyId = getIntegerInput();
        try {
            Company company = companyService.getCompanyById(companyId);
            if (company == null) {
                System.out.println("Company not found.");
                return;
            }
            System.out.println("Current company details:");
            System.out.println("Name: " + company.getCompanyName());
            System.out.println("Address: " + company.getAddress());
            System.out.println("Contact Number: " + company.getContactNumber());
            System.out.println("Email: " + company.getEmail());

            System.out.print("New company name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) company.setCompanyName(name);

            System.out.print("New address (leave blank to keep current): ");
            String address = scanner.nextLine();
            if (!address.isEmpty()) company.setAddress(address);

            System.out.print("New contact number (leave blank to keep current): ");
            String contactNumber = scanner.nextLine();
            if (!contactNumber.isEmpty()) company.setContactNumber(contactNumber);

            System.out.print("New email (leave blank to keep current): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) company.setEmail(email);

            companyService.updateCompany(company);
            System.out.println("Company updated successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
    // mini menu za triene ne kompaniq
    private static void deleteCompany() {
        System.out.print("Enter company ID to delete: ");
        int companyId = getIntegerInput();
        try {
            companyService.deleteCompany(companyId);
            System.out.println("Company deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za sgradite
    private static void handleBuildingManagement() {
        while (true) {
            System.out.println("\n=== Building Management ===");
            System.out.println("1. Create new building");
            System.out.println("2. View all buildings");
            System.out.println("3. View building details");
            System.out.println("4. Update building");
            System.out.println("5. Delete building");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntegerInput();
            switch (choice) {
                case 1: createNewBuilding(); break;
                case 2: viewAllBuildings(); break;
                case 3: viewBuildingDetails(); break;
                case 4: updateBuilding(); break;
                case 5: deleteBuilding(); break;
                case 0: return;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // mini menu za suzdavane na sgrada
    private static void createNewBuilding() {
        System.out.println("\n=== Create New Building ===");
        System.out.print("Enter employee ID responsible for the building: ");
        Integer employeeId = getIntegerInput();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter total floors: ");
        int totalFloors = getIntegerInput();
        System.out.print("Enter total apartments: ");
        int totalApartments = getIntegerInput();
        System.out.print("Enter total area: ");
        BigDecimal totalArea = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Does the building have an elevator? (yes/no): ");
        boolean hasElevator = scanner.nextLine().equalsIgnoreCase("yes");
        System.out.print("Enter description of common areas: ");
        String commonAreaDescription = scanner.nextLine();

        Building building = new Building(employeeId, address, totalFloors, totalApartments, totalArea, hasElevator, commonAreaDescription);
        try {
            buildingService.createBuilding(building);
            System.out.println("Building created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za gledane na vsichki sgradi
    private static void viewAllBuildings() {
        try {
            List<Building> buildings = buildingService.getAllBuildings();
            if (buildings.isEmpty()) {
                System.out.println("No buildings found.");
                return;
            }
            System.out.println("\nList of Buildings:");
            System.out.println("------------------");
            for (Building building : buildings) {
                System.out.printf("ID: %d, Address: %s, Floors: %d, Apartments: %d%n",
                        building.getBuildingId(), building.getAddress(), building.getTotalFloors(), building.getTotalApartments());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving buildings: " + e.getMessage());
        }
    }


    // mini menu za detailite po sgrada
    private static void viewBuildingDetails() {
        System.out.print("Enter building ID: ");
        int buildingId = getIntegerInput();
        try {
            Building building = buildingService.getBuildingById(buildingId);
            if (building == null) {
                System.out.println("Building not found.");
                return;
            }
            System.out.println("\nBuilding Details:");
            System.out.println("----------------");
            System.out.println("Address: " + building.getAddress());
            System.out.println("Total Floors: " + building.getTotalFloors());
            System.out.println("Total Apartments: " + building.getTotalApartments());
            System.out.println("Total Area: " + building.getTotalArea());
            System.out.println("Has Elevator: " + building.getHasElevator());
            System.out.println("Common Area Description: " + building.getCommonAreaDescription());
        } catch (SQLException e) {
            System.out.println("Error retrieving building details: " + e.getMessage());
        }
    }
    // mini menu za promqna na sgrada
    private static void updateBuilding() {
        System.out.print("Enter building ID to update: ");
        int buildingId = getIntegerInput();
        try {
            Building building = buildingService.getBuildingById(buildingId);
            if (building == null) {
                System.out.println("Building not found.");
                return;
            }

            System.out.println("Current building details:");
            System.out.println("Address: " + building.getAddress());
            System.out.println("Total Floors: " + building.getTotalFloors());
            System.out.println("Total Apartments: " + building.getTotalApartments());
            System.out.println("Total Area: " + building.getTotalArea());
            System.out.println("Has Elevator: " + building.getHasElevator());
            System.out.println("Common Area Description: " + building.getCommonAreaDescription());

            System.out.print("New address (leave blank to keep current): ");
            String address = scanner.nextLine();
            if (!address.isEmpty()) building.setAddress(address);

            System.out.print("New total floors (enter negative number to keep current): ");
            int totalFloors = getIntegerInput();
            if (totalFloors > 0) building.setTotalFloors(totalFloors);

            System.out.print("New total apartments (enter negative number to keep current): ");
            int totalApartments = getIntegerInput();
            if (totalApartments > 0) building.setTotalApartments(totalApartments);

            System.out.print("New total area (enter zero to keep current): ");
            BigDecimal totalArea = scanner.nextBigDecimal();
            scanner.nextLine();
            if (totalArea.compareTo(BigDecimal.ZERO) > 0) building.setTotalArea(totalArea);

            System.out.print("Has elevator? (yes/no, leave blank to keep current): ");
            String elevatorInput = scanner.nextLine();
            if (!elevatorInput.isEmpty()) {
                boolean hasElevator = elevatorInput.equalsIgnoreCase("yes");
                building.setHasElevator(hasElevator);
            }

            System.out.print("New common area description (leave blank to keep current): ");
            String commonAreaDescription = scanner.nextLine();
            if (!commonAreaDescription.isEmpty()) building.setCommonAreaDescription(commonAreaDescription);

            buildingService.updateBuilding(building);
            System.out.println("Building updated successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

// mini menu za triene na sgrada

    private static void deleteBuilding() {
        System.out.print("Enter building ID to delete: ");
        int buildingId = getIntegerInput();
        try {
            buildingService.deleteBuilding(buildingId);
            System.out.println("Building deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // mini menu za rabotnici
    private static void handleEmployeeManagement() {
        while (true) {
            System.out.println("\n=== Employee Management ===");
            System.out.println("1. Create new employee");
            System.out.println("2. View all employees");
            System.out.println("3. View employee details");
            System.out.println("4. Update employee");
            System.out.println("5. Delete employee");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntegerInput();
            switch (choice) {
                case 1:
                    createNewEmployee();
                    break;
                case 2:
                    viewAllEmployees();
                    break;
                case 3:
                    viewEmployeeDetails();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // mini menu za suzdavane na rabotnik
    private static void createNewEmployee() {
        System.out.println("\n=== Create New Employee ===");
        System.out.print("Enter company ID: ");
        Integer companyId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        Employee employee = new Employee(companyId, firstName, lastName, contactNumber, email);
        try {
            employeeService.createEmployee(employee);
            System.out.println("Employee created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za vijdane na vsichki
    private static void viewAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            if (employees.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }
            System.out.println("\nList of Employees:");
            for (Employee employee : employees) {
                System.out.printf("ID: %d, Name: %s %s, Contact: %s\n",
                        employee.getEmployeeId(), employee.getFirstName(), employee.getLastName(), employee.getContactNumber());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees: " + e.getMessage());
        }
    }
    // mini menu za detaili na rabotnika
    private static void viewEmployeeDetails() {
        System.out.print("Enter employee ID: ");
        int employeeId = getIntegerInput();
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }
            System.out.println("\nEmployee Details:");
            System.out.println("Company they work for (ID): " + employee.getCompanyId());
            System.out.println("Name: " + employee.getFirstName() + " " + employee.getLastName());
            System.out.println("Contact Number: " + employee.getContactNumber());
            System.out.println("Email: " + employee.getEmail());
        } catch (SQLException e) {
            System.out.println("Error retrieving employee details: " + e.getMessage());
        }
    }
    // mini menu za update na eployee
    private static void updateEmployee() {
        System.out.print("Enter employee ID to update: ");
        int employeeId = getIntegerInput();
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }
            System.out.println("Current employee details:");
            System.out.println("Name: " + employee.getFirstName() + " " + employee.getLastName());
            System.out.println("Contact Number: " + employee.getContactNumber());
            System.out.println("Email: " + employee.getEmail());

            System.out.print("New first name (leave blank to keep current): ");
            String firstName = scanner.nextLine();
            if (!firstName.isEmpty()) employee.setFirstName(firstName);

            System.out.print("New last name (leave blank to keep current): ");
            String lastName = scanner.nextLine();
            if (!lastName.isEmpty()) employee.setLastName(lastName);

            System.out.print("New contact number (leave blank to keep current): ");
            String contactNumber = scanner.nextLine();
            if (!contactNumber.isEmpty()) employee.setContactNumber(contactNumber);

            System.out.print("New email (leave blank to keep current): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) employee.setEmail(email);

            employeeService.updateEmployee(employee);
            System.out.println("Employee updated successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
    // mini menu za iztrivane na kolegata
    private static void deleteEmployee() {
        System.out.print("Enter employee ID to delete: ");
        int employeeId = getIntegerInput();
        try {
            employeeService.deleteEmployee(employeeId);
            System.out.println("Employee deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    // mini menu za apartamenti

    private static void handleApartmentManagement() {
        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.println("\n=== Apartment Management ===");
            System.out.println("1. Create new apartment");
            System.out.println("2. View apartment details");
            System.out.println("3. Update apartment");
            System.out.println("4. Delete apartment");
            System.out.println("5. List all apartments in a building");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    createNewApartment(scanner, apartmentService);
                    break;
                case 2:
                    viewApartmentDetails(scanner, apartmentService);
                    break;
                case 3:
                    updateApartment(scanner, apartmentService);
                    break;
                case 4:
                    deleteApartment(scanner, apartmentService);
                    break;
                case 5:
                    listApartmentsByBuilding(scanner, apartmentService);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // mini menu za suzdavane na nov apartment
    private static void createNewApartment(Scanner scanner, ApartmentService apartmentService) {
        System.out.println("\n=== Create New Apartment ===");
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter apartment number: ");
        String apartmentNumber = scanner.nextLine();
        System.out.print("Enter floor number: ");
        int floorNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter area (sqm): ");
        BigDecimal area = scanner.nextBigDecimal();
        scanner.nextLine();

        Apartment apartment = new Apartment(buildingId, apartmentNumber, floorNumber, area);
        try {
            apartmentService.createApartment(apartment);
            System.out.println("Apartment created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za detaili na apartament
    private static void viewApartmentDetails(Scanner scanner, ApartmentService apartmentService) {
        System.out.print("Enter apartment ID: ");
        int apartmentId = scanner.nextInt();
        scanner.nextLine();
        try {
            Apartment apartment = apartmentService.getApartmentById(apartmentId);
            System.out.println(apartment);
        } catch (SQLException e) {
            System.out.println("Error retrieving apartment: " + e.getMessage());
        }
    }
    // mini menu za update na apartment
    private static void updateApartment(Scanner scanner, ApartmentService apartmentService) {
        System.out.print("Enter the ID of the apartment you want to update: ");
        int apartmentId = scanner.nextInt();
        scanner.nextLine();

        try {
            Apartment currentApartment = apartmentService.getApartmentById(apartmentId);
            if (currentApartment == null) {
                System.out.println("No apartment found with ID: " + apartmentId);
                return;
            }

            // Display
            System.out.println("Current details:");
            System.out.println("Building ID: " + currentApartment.getBuildingId());
            System.out.println("Apartment Number: " + currentApartment.getApartmentNumber());
            System.out.println("Floor Number: " + currentApartment.getFloorNumber());
            System.out.println("Area (sqm): " + currentApartment.getArea());

            // Get updates
            System.out.println("Enter new building ID (Press enter to skip): ");
            String newBuildingId = scanner.nextLine();
            if (!newBuildingId.isEmpty()) currentApartment.setBuildingId(Integer.parseInt(newBuildingId));

            System.out.println("Enter new apartment number (Press enter to skip): ");
            String newApartmentNumber = scanner.nextLine();
            if (!newApartmentNumber.isEmpty()) currentApartment.setApartmentNumber(newApartmentNumber);

            System.out.println("Enter new floor number (Press enter to skip): ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) currentApartment.setFloorNumber(Integer.parseInt(input));

            System.out.println("Enter new area (sqm) (Press enter to skip): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) currentApartment.setArea(new BigDecimal(input));


            apartmentService.updateApartment(currentApartment);
            System.out.println("Apartment updated successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Input error: Please ensure you enter valid numbers.");
        }
    }

    // mini menu za triene na apartment
    private static void deleteApartment(Scanner scanner, ApartmentService apartmentService) {
        System.out.print("Enter apartment ID to delete: ");
        int apartmentId = scanner.nextInt();
        scanner.nextLine();
        try {
            apartmentService.deleteApartment(apartmentId);
            System.out.println("Apartment deleted successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za apartamenti po sgrada
    private static void listApartmentsByBuilding(Scanner scanner, ApartmentService apartmentService) {
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        scanner.nextLine();
        try {
            List<Apartment> apartments = apartmentService.getApartmentsByBuilding(buildingId);
            apartments.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println("Error retrieving apartments: " + e.getMessage());
        }
    }
    // mini menu za jivushti
    private static void handleResidentManagement() {
        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.println("\n=== Resident Management ===");
            System.out.println("1. Create new resident");
            System.out.println("2. View resident details");
            System.out.println("3. Update resident");
            System.out.println("4. Delete resident");
            System.out.println("5. List all residents in an apartment");
            System.out.println("6. List all residents in a building");
            System.out.println("7. List residents with pets in a building");
            System.out.println("8. List residents by age range in a building");
            System.out.println("9. Get total resident count in building");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    createNewResident(scanner, residentService);
                    break;
                case 2:
                    viewResidentDetails(scanner, residentService);
                    break;
                case 3:
                    updateResident(scanner, residentService);
                    break;
                case 4:
                    deleteResident(scanner);
                    break;
                case 5:
                    listResidentsByApartment(scanner);
                    break;
                case 6:
                    listResidentsByBuilding(scanner, residentService);
                    break;
                case 7:
                    listResidentsWithPets(scanner, residentService);
                    break;
                case 8:
                    listResidentsByAgeRange(scanner, residentService);
                    break;
                case 9:
                    getResidentCountInBuilding(scanner, residentService);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // mini menu za suzdavane na jivusht
    private static void createNewResident(Scanner scanner, ResidentService residentService) {
        System.out.println("\n=== Create New Resident ===");
        System.out.print("Enter apartment ID: ");
        int apartmentId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter date of birth (YYYY-MM-DD): ");
        LocalDate dob = LocalDate.parse(scanner.nextLine());
        System.out.print("Has pets? (true/false): ");
        boolean hasPet = scanner.nextBoolean();

        Resident resident = new Resident(apartmentId, firstName, lastName, dob, hasPet);
        try {
            residentService.createResident(resident);
            System.out.println("Resident created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za jivushti s pets
    private static void listResidentsWithPets(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        try {
            List<Resident> residents = residentService.getResidentsWithPets(buildingId);
            if (residents.isEmpty()) {
                System.out.println("No residents with pets found in this building.");
            } else {
                residents.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving residents with pets: " + e.getMessage());
        }
    }

    // mini menu za jivushti po vozrastni granici
    private static void listResidentsByAgeRange(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        System.out.print("Enter minimum age: ");
        int minAge = scanner.nextInt();
        System.out.print("Enter maximum age: ");
        int maxAge = scanner.nextInt();
        try {
            List<Resident> residents = residentService.getResidentsByAgeRange(buildingId, minAge, maxAge);
            if (residents.isEmpty()) {
                System.out.println("No residents found within the age range in this building.");
            } else {
                residents.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving residents by age range: " + e.getMessage());
        }
    }

    // mini menu za vseki jivusht v daden building
    private static void getResidentCountInBuilding(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        try {
            int count = residentService.getResidentCountInBuilding(buildingId);
            System.out.println("Total residents in the building: " + count);
        } catch (SQLException e) {
            System.out.println("Error retrieving resident count: " + e.getMessage());
        }
    }

    // mini menu za vijdane na jivusht
    private static void viewResidentDetails(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter resident ID: ");
        int residentId = scanner.nextInt();
        try {
            Resident resident = residentService.getResidentById(residentId);
            System.out.println(resident);
        } catch (SQLException e) {
            System.out.println("Error retrieving resident: " + e.getMessage());
        }
    }
    // mini menu za promqna na jivusht
    private static void updateResident(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter resident ID to update: ");
        int residentId = scanner.nextInt();
        scanner.nextLine();
        try {
            Resident resident = residentService.getResidentById(residentId);
            if (resident == null) {
                System.out.println("Resident not found!");
                return;
            }
            System.out.println("Current details: " + resident);

            System.out.print("Enter new first name (leave blank to keep current): ");
            String firstName = scanner.nextLine();
            if (!firstName.isEmpty()) resident.setFirstName(firstName);

            System.out.print("Enter new last name (leave blank to keep current): ");
            String lastName = scanner.nextLine();
            if (!lastName.isEmpty()) resident.setLastName(lastName);

            System.out.print("Enter new date of birth (YYYY-MM-DD) (leave blank to keep current): ");
            String dob = scanner.nextLine();
            if (!dob.isEmpty()) resident.setDateOfBirth(LocalDate.parse(dob));

            System.out.print("Has pets? (true/false) (leave blank to keep current): ");
            String hasPets = scanner.nextLine();
            if (!hasPets.isEmpty()) resident.setHasPet(Boolean.parseBoolean(hasPets));

            residentService.updateResident(resident);
            System.out.println("Resident updated successfully!");
        } catch (SQLException | ValidationException e) {
            System.out.println("Error updating resident: " + e.getMessage());
        }
    }
    // mini menu za triene na jivusht
    private static void deleteResident(Scanner scanner) {
        System.out.print("Enter resident ID to delete: ");
        int residentId = scanner.nextInt();
        try {
            ElectronicHomeManager.residentService.deleteResident(residentId);
            System.out.println("Resident deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting resident: " + e.getMessage());
        }
    }
    // mini menu za lsit na jivushti v daden apartment
    private static void listResidentsByApartment(Scanner scanner) {
        System.out.print("Enter apartment ID: ");
        int apartmentId = scanner.nextInt();
        try {
            List<Resident> residents = ElectronicHomeManager.residentService.getResidentsByApartment(apartmentId);
            residents.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println("Error retrieving residents: " + e.getMessage());
        }
    }
    // mini menu za vijdane na jivushti po sgrada
    private static void listResidentsByBuilding(Scanner scanner, ResidentService residentService) {
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        try {
            List<Resident> residents = residentService.getResidentsByBuilding(buildingId);
            residents.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println("Error retrieving residents: " + e.getMessage());
        }
    }


// mini menu za fees

    private static void handleFeeManagement() {
        Scanner scanner = new Scanner(System.in);  // Local scanner instance

        while (true) {
            System.out.println("\n=== Fee Management ===");
            System.out.println("1. Create new fee structure");
            System.out.println("2. View current fee structure for a building");
            System.out.println("3. Update fee structure");
            System.out.println("4. Delete fee structure");
            System.out.println("5. View all fee structures for a building");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createNewFeeStructure(scanner, feeService);
                    break;
                case 2:
                    viewCurrentFeeStructure(scanner, feeService);
                    break;
                case 3:
                    updateFeeStructure(scanner, feeService);
                    break;
                case 4:
                    deleteFeeStructure(scanner, feeService);
                    break;
                case 5:
                    viewFeeHistory(scanner, feeService);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // mini menu za suzdavane fees
    private static void createNewFeeStructure(Scanner scanner, FeeService feeService) {
        System.out.println("\n=== Create New Fee Structure ===");
        System.out.print("Enter building ID: ");
        int buildingId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter base rate per sqm: ");
        BigDecimal baseRate = scanner.nextBigDecimal();
        System.out.print("Enter elevator fee per person: ");
        BigDecimal elevatorFee = scanner.nextBigDecimal();
        System.out.print("Enter pet fee: ");
        BigDecimal petFee = scanner.nextBigDecimal();
        System.out.print("Enter effective date (YYYY-MM-DD): ");
        LocalDate effectiveDate = LocalDate.parse(scanner.next());
        scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD, optional): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr);

        FeeStructure feeStructure = new FeeStructure(buildingId, baseRate, elevatorFee, petFee, effectiveDate, endDate);
        try {
            feeService.createFeeStructure(feeStructure);
            System.out.println("Fee structure created successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    // mini menu za fees na sgrada
    private static void viewCurrentFeeStructure(Scanner scanner, FeeService feeService) {
        System.out.print("Enter building ID to view current fee structure: ");
        int buildingId = scanner.nextInt();
        try {
            FeeStructure feeStructure = feeService.findCurrentFee(buildingId);
            if (feeStructure != null) {
                System.out.println(feeStructure);
            } else {
                System.out.println("No current fee structure found for this building.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving fee structure: " + e.getMessage());
        }
    }
    // mini menu za update na  fees
    private static void updateFeeStructure(Scanner scanner, FeeService feeService) {
        System.out.print("Enter fee structure ID to update: ");
        int feeId = scanner.nextInt();
        scanner.nextLine();

        try {
            FeeStructure currentFee = feeService.findById(feeId);
            if (currentFee == null) {
                System.out.println("No fee structure found with ID: " + feeId);
                return;
            }


            System.out.println("Current details:");
            System.out.println("Building ID: " + currentFee.getBuildingId());
            System.out.println("Base Rate per Sqm: " + currentFee.getBaseRatePerSqm());
            System.out.println("Elevator Fee per Person: " + currentFee.getElevatorFeePerPerson());
            System.out.println("Pet Fee: " + currentFee.getPetFee());
            System.out.println("Effective Date: " + currentFee.getEffectiveDate());
            System.out.println("End Date: " + currentFee.getEndDate());


            System.out.print("Enter new building ID (leave blank to keep current): ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) currentFee.setBuildingId(Integer.parseInt(input));

            System.out.print("Enter new base rate per sqm (leave blank to keep current): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) currentFee.setBaseRatePerSqm(new BigDecimal(input));

            System.out.print("Enter new elevator fee per person (leave blank to keep current): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) currentFee.setElevatorFeePerPerson(new BigDecimal(input));

            System.out.print("Enter new pet fee (leave blank to keep current): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) currentFee.setPetFee(new BigDecimal(input));

            System.out.print("Enter new effective date (YYYY-MM-DD, leave blank to keep current): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) currentFee.setEffectiveDate(LocalDate.parse(input));

            System.out.print("Enter new end date (YYYY-MM-DD, leave blank for none): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                currentFee.setEndDate(input.isBlank() ? null : LocalDate.parse(input));
            }

            feeService.updateFeeStructure(currentFee);
            System.out.println("Fee structure updated successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Invalid input. Please make sure you are entering correct values.");
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }


    private static void deleteFeeStructure(Scanner scanner, FeeService feeService) {
        System.out.print("Enter fee structure ID to delete: ");
        int feeId = scanner.nextInt();
        try {
            feeService.delete(feeId);
            System.out.println("Fee structure deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting fee structure: " + e.getMessage());
        }
    }

    private static void viewFeeHistory(Scanner scanner, FeeService feeService) {
        System.out.print("Enter building ID to view all fee structures: ");
        int buildingId = scanner.nextInt();
        try {
            List<FeeStructure> feeStructures = feeService.findByBuilding(buildingId);
            feeStructures.forEach(System.out::println);
        } catch (SQLException e) {
            System.out.println("Error retrieving fee structures: " + e.getMessage());
        }

    }

    private static void handlePaymentManagement() {
        boolean running = true;
        while (running) {
            displayPaymentMenu();
            int choice = getIntegerInput();
            switch (choice) {
                case 1: createPayment(); break;
                case 2: updatePaymentStatus(); break;
                case 3: checkOverduePayments(); break;
                case 4: exportPaymentsToCSV(); break;
                case 5: viewPaymentDetails(); break;
                case 6: generatePaymentReport(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayPaymentMenu() {
        System.out.println("\n--- Payment Management Menu ---");
        System.out.println("1. Create a new payment");
        System.out.println("2. Update payment status");
        System.out.println("3. Check for overdue payments");
        System.out.println("4. Export payments to CSV");
        System.out.println("5. View payment details");
        System.out.println("6. Generate payment report");
        System.out.println("0. Back to main menu");
        System.out.print("Enter your choice: ");
    }

    private static void createPayment() {
        try {
            System.out.print("Enter Apartment ID: ");
            int apartmentId = getIntegerInput();

            // Access FeeService through PaymentService to calculate the expected amount
            BigDecimal expectedAmount = paymentService.getFeeService().calculateMonthlyFee(apartmentId);
            System.out.printf("Expected Payment Amount: %.2f%n", expectedAmount);

            System.out.print("Enter Payment Amount: ");
            BigDecimal amount = scanner.nextBigDecimal();

            System.out.print("Enter Payment Month (YYYY-MM-DD): ");
            scanner.nextLine(); // Consume newline left by nextBigDecimal
            LocalDate paymentMonth = LocalDate.parse(scanner.nextLine());

            Payment payment = new Payment(apartmentId, amount, paymentMonth);
            paymentService.createPayment(payment);
            System.out.println("Payment successfully created.");
        } catch (ValidationException | SQLException e) {
            System.err.println("Error creating payment: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Invalid input. Please try again.");
            scanner.nextLine(); // Clear invalid input
        }
    }


    private static void updatePaymentStatus() {
        try {
            System.out.print("Enter Payment ID: ");
            int paymentId = getIntegerInput();
            System.out.print("Enter new status (PAID, PENDING, OVERDUE): ");
            String status = scanner.nextLine();

            paymentService.updatePaymentStatus(paymentId, status);
            System.out.println("Payment status updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
        }
    }

    private static void checkOverduePayments() {
        try {
            paymentService.checkOverduePayments();
            System.out.println("Overdue payments checked and updated.");
        } catch (SQLException e) {
            System.err.println("Error checking overdue payments: " + e.getMessage());
        }
    }

    private static void exportPaymentsToCSV() {
        try {
            System.out.print("Enter file path to export payments (e.g., payments.csv): ");
            String filePath = scanner.nextLine();

            List<Payment> payments = paymentService.getPaymentsByApartment(null); // Fetch all payments
            paymentService.exportPaymentsToCSV(payments, filePath);
            System.out.println("Payments exported successfully to " + filePath);
        } catch (IOException | SQLException e) {
            System.err.println("Error exporting payments: " + e.getMessage());
        }
    }

    private static void viewPaymentDetails() {
        try {
            System.out.print("Enter Payment ID: ");
            int paymentId = getIntegerInput();

            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment != null) {
                System.out.println(payment);
            } else {
                System.out.println("Payment not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment details: " + e.getMessage());
        }
    }

    private static void generatePaymentReport() {
        try {
            System.out.print("Enter Company ID: ");
            int companyId = getIntegerInput();
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            List<Payment> payments = paymentService.generatePaymentReport(companyId, startDate, endDate);
            System.out.println("\n--- Payment Report ---");
            payments.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("Error generating payment report: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Invalid input. Please ensure dates are in the correct format.");
        }
    }

}

 class ApartmentOwnershipManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ApartmentOwnershipService apartmentOwnershipService = new ApartmentOwnershipService();

    public static void handleApartmentOwnershipManagement() {
        while (true) {
            System.out.println("\n=== Apartment Ownership Management ===");
            System.out.println("1. Add new ownership");
            System.out.println("2. View ownership history");
            System.out.println("3. View current ownership");
            System.out.println("4. End ownership");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntegerInput();
            switch (choice) {
                case 1: addNewOwnership(); break;
                case 2: viewOwnershipHistory(); break;
                case 3: viewCurrentOwnership(); break;
                case 4: endOwnership(); break;
                case 0: return;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNewOwnership() {
        System.out.println("\n=== Add New Ownership ===");
        System.out.print("Enter apartment ID: ");
        int apartmentId = getIntegerInput();
        System.out.print("Enter owner ID: ");
        int ownerId = getIntegerInput();
        System.out.print("Enter ownership start date (YYYY-MM-DD): ");
        String startDateInput = scanner.nextLine();

        com.homemanager.model.ApartmentOwnershipManager ownership = new com.homemanager.model.ApartmentOwnershipManager(apartmentId, ownerId, LocalDate.parse(startDateInput));
        try {
            apartmentOwnershipService.createOwnership(ownership);
            System.out.println("Ownership added successfully!");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewOwnershipHistory() {
        System.out.print("Enter apartment ID: ");
        int apartmentId = getIntegerInput();
        try {
            List<com.homemanager.model.ApartmentOwnershipManager> ownerships = apartmentOwnershipService.getOwnershipHistory(apartmentId);
            if (ownerships.isEmpty()) {
                System.out.println("No ownership history found for the given apartment ID.");
                return;
            }
            System.out.println("\nOwnership History:");
            for (com.homemanager.model.ApartmentOwnershipManager ownership : ownerships) {
                System.out.printf("Apartment ID: %d, Owner ID: %d, Start Date: %s, End Date: %s\n",
                        ownership.getApartmentId(),
                        ownership.getOwnerId(),
                        ownership.getStartDate(),
                        ownership.getEndDate() == null ? "N/A" : ownership.getEndDate());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving ownership history: " + e.getMessage());
        }
    }

    private static void viewCurrentOwnership() {
        System.out.print("Enter apartment ID: ");
        int apartmentId = getIntegerInput();
        try {
            com.homemanager.model.ApartmentOwnershipManager ownership = apartmentOwnershipService.getCurrentOwnership(apartmentId);
            if (ownership == null) {
                System.out.println("No current ownership found for the given apartment ID.");
                return;
            }
            System.out.println("\nCurrent Ownership:");
            System.out.printf("Apartment ID: %d, Owner ID: %d, Start Date: %s\n",
                    ownership.getApartmentId(),
                    ownership.getOwnerId(),
                    ownership.getStartDate());
        } catch (SQLException e) {
            System.out.println("Error retrieving current ownership: " + e.getMessage());
        }
    }

    private static void endOwnership() {
        System.out.print("Enter apartment ID: ");
        int apartmentId = getIntegerInput();
        System.out.print("Enter owner ID: ");
        int ownerId = getIntegerInput();
        System.out.print("Enter ownership end date (YYYY-MM-DD): ");
        String endDateInput = scanner.nextLine();

        try {
            apartmentOwnershipService.endOwnership(apartmentId, ownerId, LocalDate.parse(endDateInput));
            System.out.println("Ownership ended successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static int getIntegerInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.println("Invalid input. Please enter a number.");
        }
        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }
}


