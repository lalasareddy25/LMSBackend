package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.QuestionRequest;
import com.example.demo.entity.Course;
import com.example.demo.entity.Questions;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.QuestionRepository;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public QuestionController(QuestionRepository questionRepository, CourseRepository courseRepository) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/api/questionsenter")
    public ResponseEntity<String> addQuestion(@RequestBody QuestionRequest questionRequest) {
        // Check if course exists
        Course course = courseRepository.findById(questionRequest.getCourseId()).orElse(null);
        if (course == null) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        // Create and save question
        Questions question = new Questions();
        question.setQuestion(questionRequest.getQuestion());
        question.setOption1(questionRequest.getOption1());
        question.setOption2(questionRequest.getOption2());
        question.setOption3(questionRequest.getOption3());
        question.setOption4(questionRequest.getOption4());
        question.setAnswer(questionRequest.getAnswer());
        question.setCourse(course);

        Questions savedQuestion = questionRepository.save(question);
        if (savedQuestion == null) {
            return new ResponseEntity<>("Failed to add question", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
    }

    
    @GetMapping("/{courseId}")
    public ResponseEntity<List<Questions>> getAllQuestionsForCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);

        // If the course is not found, return a 404 response
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Retrieve questions for the course
        List<Questions> questions = questionRepository.findByCourse(course);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

}
