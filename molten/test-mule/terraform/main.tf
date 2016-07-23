variable "file" {
	default = "../target/test-mule-0.0.1-SNAPSHOT.jar"
}

provider "aws" {
	region = "ap-southeast-2"
}

resource "aws_iam_role" "iam_for_lambda" {
    name = "iam_for_lambda"
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_lambda_function" "test_lambda" {
    filename = "${var.file}"
    function_name = "HelloMule"
    role = "${aws_iam_role.iam_for_lambda.arn}"
    handler = "LambdaFunctionHandler.handleRequest"
	runtime = "Java8"
}