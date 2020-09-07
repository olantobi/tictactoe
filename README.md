### What libraries and/or frameworks you chose for the project and why?

Spring boot framework was used for this project because it is relatively fast and also has all the components required for a modern web api.

### Which part of the task you found the most difficult. Why? How did you solve it?

The part of the task that was a bit challenging was the place mark endpoint. This is where the core logic of the came resides.

I designed the game on paper and analyzed the player interactions with the gameboard. The data structure closest to the matrix layout of the game board is a 2-dimensional array. I also analyzed the WIN conditions and transferred that to the code. The winning conditions are checked after every mark placement to see if a player has won.

### What would you need to change in the code if the next feature was to generalize the game to NxN size boards?

What I would need to change is the size of the 2-dimensional array and then update the winning conditions array list.