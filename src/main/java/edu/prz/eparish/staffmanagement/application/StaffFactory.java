package edu.prz.eparish.staffmanagement.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.staffmanagement.domain.duty.Duty;
import edu.prz.eparish.staffmanagement.domain.duty.DutyRepository;
import edu.prz.eparish.staffmanagement.domain.employee.Employee;
import edu.prz.eparish.staffmanagement.domain.employee.EmployeeRepository;
import edu.prz.eparish.staffmanagement.domain.position.Position;
import edu.prz.eparish.staffmanagement.domain.position.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StaffFactory {

  private final EmployeeRepository employeeRepo;
  private final PositionRepository positionRepo;
  private final DutyRepository dutyRepo;

  public Employee createEmployee(String firstName, String lastName, Parish parish, Position position) {
    Employee employee = new Employee();
    employee.setId(EntityIds.nextId(employeeRepo, Employee::getId));
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setParish(parish);
    employee.setPosition(position);
    return employee;
  }

  public Position createPosition(String name, String description) {
    Position position = new Position();
    position.setId(EntityIds.nextId(positionRepo, Position::getId));
    position.setName(name);
    position.setDescription(description);
    return position;
  }

  public Duty createDuty(String name, String description, Position position) {
    Duty duty = new Duty();
    duty.setId(EntityIds.nextId(dutyRepo, Duty::getId));
    duty.setName(name);
    duty.setDescription(description);
    duty.setPosition(position);
    duty.setStatus("ASSIGNED");
    return duty;
  }
}
