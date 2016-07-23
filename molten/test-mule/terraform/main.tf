variable "file" {
	default = "../target/test-mule-0.0.1-SNAPSHOT.jar"
}

variable "region" {
	default = "ap-southeast-2"
}

provider "aws" {
	region = "${var.region}"
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

resource "aws_api_gateway_rest_api" "test_api" {
  name = "TestAPI"
  description = "This is the Test API"
}

resource "aws_api_gateway_resource" "test" {
  rest_api_id = "${aws_api_gateway_rest_api.test_api.id}"
  parent_id = "${aws_api_gateway_rest_api.test_api.root_resource_id}"
  path_part = "test"
}

resource "aws_api_gateway_resource" "test_test" {
  rest_api_id = "${aws_api_gateway_rest_api.test_api.id}"
  parent_id = "${aws_api_gateway_resource.test.id}"
  path_part = "test"
}

resource "aws_api_gateway_method" "test_test-get" {
  rest_api_id = "${aws_api_gateway_rest_api.test_api.id}"
  resource_id = "${aws_api_gateway_resource.test_test.id}"
  http_method = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "test_test-get-integration" {
  rest_api_id = "${aws_api_gateway_rest_api.test_api.id}"
  resource_id = "${aws_api_gateway_resource.test_test.id}"
  http_method = "${aws_api_gateway_method.test_test-get.http_method}"
  type = "AWS"
  uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.test_lambda.arn}/invocations"
  integration_http_method = "${aws_api_gateway_method.test_test-get.http_method}"
}