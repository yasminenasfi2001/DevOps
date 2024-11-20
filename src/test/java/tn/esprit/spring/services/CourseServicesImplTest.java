package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServicesImplTest {

    @InjectMocks
    private CourseServicesImpl courseServices;

    @Mock
    private ICourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllCourses() {
        Course course1 = Course.builder().numCourse(1L).level(2).typeCourse(TypeCourse.INDIVIDUAL).support(Support.SKI).price(150.0f).timeSlot(10).build();
        Course course2 = Course.builder().numCourse(2L).level(3).typeCourse(TypeCourse.COLLECTIVE_ADULT).support(Support.SNOWBOARD).price(200.0f).timeSlot(12).build();
        List<Course> expectedCourses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(expectedCourses);

        List<Course> actualCourses = courseServices.retrieveAllCourses();

        verify(courseRepository, times(1)).findAll();
        assertThat(actualCourses).isEqualTo(expectedCourses);
    }

    @Test
    void testAddCourse() {
        Course course = Course.builder().level(1).typeCourse(TypeCourse.COLLECTIVE_CHILDREN).support(Support.SKI).price(100.0f).timeSlot(8).build();

        when(courseRepository.save(course)).thenReturn(course);

        Course actualCourse = courseServices.addCourse(course);

        assertNotNull(actualCourse);
        assertEquals(course, actualCourse);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testUpdateCourse() {
        Course course = Course.builder().numCourse(1L).level(2).typeCourse(TypeCourse.INDIVIDUAL).support(Support.SKI).price(150.0f).timeSlot(10).build();

        when(courseRepository.save(course)).thenReturn(course);

        Course updatedCourse = courseServices.updateCourse(course);

        assertNotNull(updatedCourse);
        assertEquals(course, updatedCourse);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testRetrieveCourse() {
        Long courseId = 1L;
        Course expectedCourse = Course.builder().numCourse(courseId).level(3).typeCourse(TypeCourse.INDIVIDUAL).support(Support.SKI).price(180.0f).timeSlot(14).build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(expectedCourse));

        Course actualCourse = courseServices.retrieveCourse(courseId);

        assertNotNull(actualCourse);
        assertEquals(expectedCourse, actualCourse);
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void testRetrieveCourseNotFound() {
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        Course result = courseServices.retrieveCourse(courseId);

        assertThat(result).isNull();
        verify(courseRepository, times(1)).findById(courseId);
    }
}