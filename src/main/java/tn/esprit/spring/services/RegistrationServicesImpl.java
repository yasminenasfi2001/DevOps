package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
@Slf4j
@AllArgsConstructor
@Service
@Generated

public class RegistrationServicesImpl implements  IRegistrationServices{

    private IRegistrationRepository registrationRepository;
    private ISkierRepository skierRepository;
    private ICourseRepository courseRepository;


    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        registration.setSkier(skier);
        return registrationRepository.save(registration);
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        Registration registration = registrationRepository.findById(numRegistration).orElse(null);
        Course course = courseRepository.findById(numCourse).orElse(null);

        if (registration == null || course == null) {
            log.warn("Registration or course not found. Registration ID: " + numRegistration + ", Course ID: " + numCourse);
            return null; // Return null or throw an exception depending on your error handling preference
        }

        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Course course = courseRepository.findById(numCours).orElse(null);

        if (skier == null || course == null) {
            return null;
        }

        if (registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1) {
            log.info("Sorry, you're already registered for this course in week: " + registration.getNumWeek());
            return null;
        }

        int ageSkieur = Period.between(skier.getDateOfBirth(), LocalDate.now()).getYears();
        log.info("Age: " + ageSkieur);

        if (course.getTypeCourse() == TypeCourse.INDIVIDUAL) {
            log.info("Individual course, adding registration without age checks.");
            return assignRegistration(registration, skier, course);
        }

        boolean isChildCourse = course.getTypeCourse() == TypeCourse.COLLECTIVE_CHILDREN;
        boolean isFullCourse = registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) >= 6;

        if ((isChildCourse && ageSkieur < 16) || (!isChildCourse && ageSkieur >= 16)) {
            if (!isFullCourse) {
                log.info("Course successfully added!");
                return assignRegistration(registration, skier, course);
            } else {
                log.info("Full Course! Please choose another week.");
                return null;
            }
        } else {
            log.info("Sorry, your age doesn't match the requirements for this course.");
        }

        return null;
    }

    private Registration assignRegistration(Registration registration, Skier skier, Course course) {
        registration.setSkier(skier);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }


    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        return registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }

}
