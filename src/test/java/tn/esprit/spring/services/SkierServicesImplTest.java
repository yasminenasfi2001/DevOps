package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class SkierServicesImplTest {
    @InjectMocks
    private SkierServicesImpl skierServices;
    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testRetrieveAllSkiers() {
        Skier skier1 = Skier.builder().firstName("Ahmed").lastName("Nasfi").dateOfBirth(LocalDate.of(2001, 1, 18)).city("Le Havre").build();
        Skier skier2 = Skier.builder().firstName("Amir").lastName("Karim").dateOfBirth(LocalDate.of(2005, 4, 7)).city("Toulouse").build();
        List<Skier> expectedSkiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findAll()).thenReturn(expectedSkiers);
        List<Skier> actualSkiers = skierServices.retrieveAllSkiers();
        verify(skierRepository).findAll();
        assertThat(actualSkiers).isEqualTo(expectedSkiers);
    }

    @Test
    void testAddSkier() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        //Annual Subscription test
        Subscription annualSubscription = Subscription.builder().typeSub(TypeSubscription.ANNUAL).startDate(startDate).build();
        Skier skierAnnualExpected = Skier.builder().firstName("John").lastName("Doe").subscription(annualSubscription).build();
        when(skierRepository.save(skierAnnualExpected)).thenReturn(skierAnnualExpected);
        Skier skierAnnualActual = skierServices.addSkier(skierAnnualExpected);
        assertNotNull(skierAnnualActual);
        assertEquals(LocalDate.of(2025, 1, 1), skierAnnualActual.getSubscription().getEndDate());
        //Semestrial subscription test
        Subscription semesterSubscription = Subscription.builder().typeSub(TypeSubscription.SEMESTRIEL).startDate(startDate).build();
        Skier skierSemesterExpected = Skier.builder().firstName("Jane").lastName("Doe").subscription(semesterSubscription).build();
        when(skierRepository.save(skierSemesterExpected)).thenReturn(skierSemesterExpected);
        Skier skierSemesterActual = skierServices.addSkier(skierSemesterExpected);
        assertNotNull(skierSemesterActual);
        assertEquals(LocalDate.of(2024, 7, 1), skierSemesterActual.getSubscription().getEndDate());
        //Monthly subscription test
        Subscription monthlySubscription = Subscription.builder().typeSub(TypeSubscription.MONTHLY).startDate(startDate).build();
        Skier skierMonthlyExpected = Skier.builder().firstName("Alice").lastName("Doe").subscription(monthlySubscription).build();
        when(skierRepository.save(skierMonthlyExpected)).thenReturn(skierMonthlyExpected);
        Skier skierMonthlyActual = skierServices.addSkier(skierMonthlyExpected);
        assertNotNull(skierMonthlyActual);
        assertEquals(LocalDate.of(2024, 2, 1), skierMonthlyActual.getSubscription().getEndDate());

    }

    @Test
    void testAssignSkierToSubscription() {
        Subscription subscriptionAssign = Subscription.builder().numSub(1L).typeSub(TypeSubscription.ANNUAL).startDate(LocalDate.now()).build();
        Skier skierAssign = Skier.builder().numSkier(1L).firstName("Jad").lastName("lamine").dateOfBirth(LocalDate.of(1990, 1, 1)).city("Paris").subscription(null).registrations(new HashSet<>()).build();
        when(skierRepository.findById(skierAssign.getNumSkier())).thenReturn(Optional.of(skierAssign));
        when(subscriptionRepository.findById(subscriptionAssign.getNumSub())).thenReturn(Optional.of(subscriptionAssign));
        when(skierRepository.save(skierAssign)).thenReturn(skierAssign);
        Skier skierSubscribed = skierServices.assignSkierToSubscription(skierAssign.getNumSkier(), subscriptionAssign.getNumSub());
        assertNotNull(skierSubscribed);
        assertEquals(subscriptionAssign, skierSubscribed.getSubscription());
    }

    @Test
    void testAddSkierAndAssignToCourse() {
        Skier skier = Skier.builder().numSkier(3L).firstName("Ahmed").lastName("You").dateOfBirth(LocalDate.of(2003, 1, 1)).city("Nice").subscription(null).registrations(new HashSet<>()).build();
        Course course = Course.builder().numCourse(2L).level(1).typeCourse(TypeCourse.INDIVIDUAL).support(Support.SKI).build();
        Registration registration = new Registration();
        skier.getRegistrations().add(registration);
        when(skierRepository.save(skier)).thenReturn(skier);
        when(courseRepository.getById(course.getNumCourse())).thenReturn(course);
        when(registrationRepository.save(registration)).thenReturn(registration);
        Skier actualSkier = skierServices.addSkierAndAssignToCourse(skier, course.getNumCourse());
        Registration assignedRegistration = actualSkier.getRegistrations().iterator().next();
        assertNotNull(actualSkier);
        assertEquals(1, actualSkier.getRegistrations().size());
        assertEquals(skier, assignedRegistration.getSkier());
        assertEquals(course, assignedRegistration.getCourse());
    }

    @Test
    void testRemoveSkier() {
        Skier skier = Skier.builder().numSkier(3L).firstName("Ahmed").lastName("You").dateOfBirth(LocalDate.of(2003, 1, 1)).city("Nice").subscription(null).registrations(new HashSet<>()).build();
        skierServices.removeSkier(skier.getNumSkier());
        verify(skierRepository, times(1)).deleteById(skier.getNumSkier());
    }

    @Test
    void testRetrieveSkier() {
        Skier expectedSkier = Skier.builder().numSkier(1L).firstName("Amir").lastName("Nasfi").dateOfBirth(LocalDate.of(1990, 1, 1)).city("Paris").subscription(null).registrations(new HashSet<>()).build();
        when(skierRepository.findById(expectedSkier.getNumSkier())).thenReturn(Optional.of(expectedSkier));
        Skier actualSkier = skierServices.retrieveSkier(expectedSkier.getNumSkier());
        assertNotNull(actualSkier);
        assertEquals("Nasfi", actualSkier.getLastName());
        assertThat(expectedSkier).isEqualTo(actualSkier);
    }
    @Test
    void testAssignSkierToPiste() {
        Skier skier = Skier.builder().numSkier(1L).pistes(new HashSet<>()).build();
        Piste piste = Piste.builder().numPiste(2L).namePiste("singp").build();
        when(skierRepository.findById(skier.getNumSkier())).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(piste.getNumPiste())).thenReturn(Optional.of(piste));
        when(skierRepository.save(skier)).thenReturn(skier);
        Skier skierAssignedToPiste = skierServices.assignSkierToPiste(skier.getNumSkier(), piste.getNumPiste());
        assertNotNull(skierAssignedToPiste);
        assertTrue(skierAssignedToPiste.getPistes().contains(piste));
        //test skier not found condition
        when(skierRepository.findById(skier.getNumSkier())).thenReturn(Optional.empty());
        Skier result = skierServices.assignSkierToPiste(skier.getNumSkier(), piste.getNumPiste());
        assertNull(result);
        //test piste not found condition
        when(skierRepository.findById(skier.getNumSkier())).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(piste.getNumPiste())).thenReturn(Optional.empty());
        result = skierServices.assignSkierToPiste(skier.getNumSkier(), piste.getNumPiste());
        assertNull(result);
        //test skier with null piste condition
        skier = Skier.builder().numSkier(1L).pistes(null).build();
        when(skierRepository.findById(skier.getNumSkier())).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(piste.getNumPiste())).thenReturn(Optional.of(piste));
        when(skierRepository.save(skier)).thenReturn(skier);
        skierAssignedToPiste = skierServices.assignSkierToPiste(skier.getNumSkier(), piste.getNumPiste());
        assertNotNull(skierAssignedToPiste.getPistes());
        assertTrue(skierAssignedToPiste.getPistes().contains(piste));
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        TypeSubscription typeSubscription = TypeSubscription.ANNUAL;
        Skier skier1 = Skier.builder().numSkier(1L).firstName("Amir").lastName("Feres").subscription(Subscription.builder().typeSub(TypeSubscription.ANNUAL).build()).build();
        Skier skier2 = Skier.builder().numSkier(2L).firstName("Yassine").lastName("Abd").subscription(Subscription.builder().typeSub(TypeSubscription.ANNUAL).build()).build();
        List<Skier> expectedSkiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findBySubscription_TypeSub(typeSubscription)).thenReturn(expectedSkiers);
        List<Skier> actualSkiers = skierServices.retrieveSkiersBySubscriptionType(typeSubscription);
        assertNotNull(actualSkiers);
        assertEquals(2, actualSkiers.size());
        assertNotNull(actualSkiers);
        for (Skier skier : actualSkiers) {
            assertEquals(TypeSubscription.ANNUAL, skier.getSubscription().getTypeSub());
        }
            }

}