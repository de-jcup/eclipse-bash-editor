# +++++++++++++++++++++++++++++++++++++++++++++
# Test single lines:
# +++++++++++++++++++++++++++++++++++++++++++++
echo This shouldn\'t be a problem
echo 'Or did you expect this to be?'

# +++++++++++++++++++++++++++++++++++++++++++++
# Test multi lines + different string tokens:
# +++++++++++++++++++++++++++++++++++++++++++++

# Simple String
echo This shouldn\'t be a problem
echo 'Or did you expect this to be? By the
way a string can be multilined in bash'

# Double String
echo This shouldn\"t be a problem
echo "Or did you expect this to be? By the
way a double string can be multilined in bash"

# Ticked String
echo This shouldn\`t be a problem
echo `Or did you expect this to be? By the
way a double string can be multilined in bash`

# End