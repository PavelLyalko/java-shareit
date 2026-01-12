package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentResponse toCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());
        commentResponse.setAuthorName(comment.getAuthor().getName());
        commentResponse.setCreated(comment.getCreated());
        return commentResponse;
    }
}
