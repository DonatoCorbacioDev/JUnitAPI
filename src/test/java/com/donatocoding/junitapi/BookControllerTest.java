package com.donatocoding.junitapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;
    
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookController bookController;
    
    Book RECORD_1 = new Book(1L, "Atomic Habits", "How to build better habits", 5);
    Book RECORD_2 = new Book(2L, "Thinking Fast and Slow", "How to create good mental models about thinking", 4);
    Book RECORD_3 = new Book(3L, "Grokking Algorithms", "Learn algorithms the fun way", 5);
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllRecords_success() throws Exception {
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));

        Mockito.when(bookRepository.findAll()).thenReturn(records);

        mockMvc.perform(get("/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is("Grokking Algorithms")))
                .andExpect(jsonPath("$[1].name", is("Thinking Fast and Slow")));
    }
    
    @Test
    public void getBookById_success() throws Exception {
    	Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(java.util.Optional.of(RECORD_1));
    	
    	mockMvc.perform(get("/book/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Atomic Habits")));
    }
    
    @Test
    public void getBookById_notFound() {}
    
    @Test
    public void createRecord_success() throws Exception {
    	Book record = Book.builder()
    			.bookId(4L)
    			.name("Introduction to C")
    			.summary("The name but longer")
    			.rating(5)
    			.build();
    	
    	Mockito.when(bookRepository.save(record)).thenReturn(record);
    	
    	String content = objectWriter.writeValueAsString(record);
    	
    	MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/book")
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)
    			.content(content);
    	
    	mockMvc.perform(mockRequest)
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Introduction to C")));
    }
    
    @Test
    public void updateBookRecord_success() throws Exception {
    	Book updatedRecord = Book.builder()
    			.bookId(1L)
    			.name("Updated Book Name")
    			.summary("Updated Summary")
    			.rating(1).build();
    	
    	Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(java.util.Optional.ofNullable(RECORD_1));
    	Mockito.when(bookRepository.save(updatedRecord)).thenReturn(updatedRecord);
    	
    	String updatedContent = objectWriter.writeValueAsString(updatedRecord);
    	
    	MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/book")
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)
    			.content(updatedContent);
    	
    	mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Updated Book Name")));
    }
    
    @Test
    public void deleteBookById_success() throws Exception {
    	Mockito.when(bookRepository.findById(RECORD_2.getBookId())).thenReturn(java.util.Optional.ofNullable(RECORD_2));
    	
    	mockMvc.perform(MockMvcRequestBuilders
    	       .delete("/book/2")
    	       .contentType(MediaType.APPLICATION_JSON))
    	       .andExpect(status().isOk());
    }
  
}
