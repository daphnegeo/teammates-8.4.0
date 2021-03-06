package com.google.appengine.logging.v1;

import com.google.appengine.logging.v1.RequestLog.Builder;

public interface Case0 {

	com.google.protobuf.UnknownFieldSet getUnknownFields();

	int APP_ID_FIELD_NUMBER = 1;

	/**
	   * <pre>
	   * Application that handled this request.
	   * </pre>
	   *
	   * <code>string app_id = 1;</code>
	   */
	java.lang.String getAppId();

	/**
	   * <pre>
	   * Application that handled this request.
	   * </pre>
	   *
	   * <code>string app_id = 1;</code>
	   */
	com.google.protobuf.ByteString getAppIdBytes();

	int MODULE_ID_FIELD_NUMBER = 37;

	/**
	   * <pre>
	   * Module of the application that handled this request.
	   * </pre>
	   *
	   * <code>string module_id = 37;</code>
	   */
	java.lang.String getModuleId();

	/**
	   * <pre>
	   * Module of the application that handled this request.
	   * </pre>
	   *
	   * <code>string module_id = 37;</code>
	   */
	com.google.protobuf.ByteString getModuleIdBytes();

	int VERSION_ID_FIELD_NUMBER = 2;

	/**
	   * <pre>
	   * Version of the application that handled this request.
	   * </pre>
	   *
	   * <code>string version_id = 2;</code>
	   */
	java.lang.String getVersionId();

	/**
	   * <pre>
	   * Version of the application that handled this request.
	   * </pre>
	   *
	   * <code>string version_id = 2;</code>
	   */
	com.google.protobuf.ByteString getVersionIdBytes();

	int REQUEST_ID_FIELD_NUMBER = 3;

	/**
	   * <pre>
	   * Globally unique identifier for a request, which is based on the request
	   * start time.  Request IDs for requests which started later will compare
	   * greater as strings than those for requests which started earlier.
	   * </pre>
	   *
	   * <code>string request_id = 3;</code>
	   */
	java.lang.String getRequestId();

	/**
	   * <pre>
	   * Globally unique identifier for a request, which is based on the request
	   * start time.  Request IDs for requests which started later will compare
	   * greater as strings than those for requests which started earlier.
	   * </pre>
	   *
	   * <code>string request_id = 3;</code>
	   */
	com.google.protobuf.ByteString getRequestIdBytes();

	int IP_FIELD_NUMBER = 4;

	/**
	   * <pre>
	   * Origin IP address.
	   * </pre>
	   *
	   * <code>string ip = 4;</code>
	   */
	java.lang.String getIp();

	/**
	   * <pre>
	   * Origin IP address.
	   * </pre>
	   *
	   * <code>string ip = 4;</code>
	   */
	com.google.protobuf.ByteString getIpBytes();

	int START_TIME_FIELD_NUMBER = 6;

	/**
	   * <pre>
	   * Time when the request started.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp start_time = 6;</code>
	   */
	boolean hasStartTime();

	/**
	   * <pre>
	   * Time when the request started.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp start_time = 6;</code>
	   */
	com.google.protobuf.Timestamp getStartTime();

	/**
	   * <pre>
	   * Time when the request started.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp start_time = 6;</code>
	   */
	com.google.protobuf.TimestampOrBuilder getStartTimeOrBuilder();

	int END_TIME_FIELD_NUMBER = 7;

	/**
	   * <pre>
	   * Time when the request finished.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp end_time = 7;</code>
	   */
	boolean hasEndTime();

	/**
	   * <pre>
	   * Time when the request finished.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp end_time = 7;</code>
	   */
	com.google.protobuf.Timestamp getEndTime();

	/**
	   * <pre>
	   * Time when the request finished.
	   * </pre>
	   *
	   * <code>.google.protobuf.Timestamp end_time = 7;</code>
	   */
	com.google.protobuf.TimestampOrBuilder getEndTimeOrBuilder();

	int LATENCY_FIELD_NUMBER = 8;

	/**
	   * <pre>
	   * Latency of the request.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration latency = 8;</code>
	   */
	boolean hasLatency();

	/**
	   * <pre>
	   * Latency of the request.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration latency = 8;</code>
	   */
	com.google.protobuf.Duration getLatency();

	/**
	   * <pre>
	   * Latency of the request.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration latency = 8;</code>
	   */
	com.google.protobuf.DurationOrBuilder getLatencyOrBuilder();

	int MEGA_CYCLES_FIELD_NUMBER = 9;

	/**
	   * <pre>
	   * Number of CPU megacycles used to process request.
	   * </pre>
	   *
	   * <code>int64 mega_cycles = 9;</code>
	   */
	long getMegaCycles();

	int METHOD_FIELD_NUMBER = 10;

	/**
	   * <pre>
	   * Request method. Example: `"GET"`, `"HEAD"`, `"PUT"`, `"POST"`, `"DELETE"`.
	   * </pre>
	   *
	   * <code>string method = 10;</code>
	   */
	java.lang.String getMethod();

	/**
	   * <pre>
	   * Request method. Example: `"GET"`, `"HEAD"`, `"PUT"`, `"POST"`, `"DELETE"`.
	   * </pre>
	   *
	   * <code>string method = 10;</code>
	   */
	com.google.protobuf.ByteString getMethodBytes();

	int RESOURCE_FIELD_NUMBER = 11;

	/**
	   * <pre>
	   * Contains the path and query portion of the URL that was requested. For
	   * example, if the URL was "http://example.com/app?name=val", the resource
	   * would be "/app?name=val".  The fragment identifier, which is identified by
	   * the `#` character, is not included.
	   * </pre>
	   *
	   * <code>string resource = 11;</code>
	   */
	java.lang.String getResource();

	/**
	   * <pre>
	   * Contains the path and query portion of the URL that was requested. For
	   * example, if the URL was "http://example.com/app?name=val", the resource
	   * would be "/app?name=val".  The fragment identifier, which is identified by
	   * the `#` character, is not included.
	   * </pre>
	   *
	   * <code>string resource = 11;</code>
	   */
	com.google.protobuf.ByteString getResourceBytes();

	int HTTP_VERSION_FIELD_NUMBER = 12;

	/**
	   * <pre>
	   * HTTP version of request. Example: `"HTTP/1.1"`.
	   * </pre>
	   *
	   * <code>string http_version = 12;</code>
	   */
	java.lang.String getHttpVersion();

	/**
	   * <pre>
	   * HTTP version of request. Example: `"HTTP/1.1"`.
	   * </pre>
	   *
	   * <code>string http_version = 12;</code>
	   */
	com.google.protobuf.ByteString getHttpVersionBytes();

	int STATUS_FIELD_NUMBER = 13;

	/**
	   * <pre>
	   * HTTP response status code. Example: 200, 404.
	   * </pre>
	   *
	   * <code>int32 status = 13;</code>
	   */
	int getStatus();

	int RESPONSE_SIZE_FIELD_NUMBER = 14;

	/**
	   * <pre>
	   * Size in bytes sent back to client by request.
	   * </pre>
	   *
	   * <code>int64 response_size = 14;</code>
	   */
	long getResponseSize();

	int REFERRER_FIELD_NUMBER = 15;

	/**
	   * <pre>
	   * Referrer URL of request.
	   * </pre>
	   *
	   * <code>string referrer = 15;</code>
	   */
	java.lang.String getReferrer();

	/**
	   * <pre>
	   * Referrer URL of request.
	   * </pre>
	   *
	   * <code>string referrer = 15;</code>
	   */
	com.google.protobuf.ByteString getReferrerBytes();

	int USER_AGENT_FIELD_NUMBER = 16;

	/**
	   * <pre>
	   * User agent that made the request.
	   * </pre>
	   *
	   * <code>string user_agent = 16;</code>
	   */
	java.lang.String getUserAgent();

	/**
	   * <pre>
	   * User agent that made the request.
	   * </pre>
	   *
	   * <code>string user_agent = 16;</code>
	   */
	com.google.protobuf.ByteString getUserAgentBytes();

	int NICKNAME_FIELD_NUMBER = 40;

	/**
	   * <pre>
	   * The logged-in user who made the request.
	   * Most likely, this is the part of the user's email before the `&#64;` sign.  The
	   * field value is the same for different requests from the same user, but
	   * different users can have similar names.  This information is also
	   * available to the application via the App Engine Users API.
	   * This field will be populated starting with App Engine 1.9.21.
	   * </pre>
	   *
	   * <code>string nickname = 40;</code>
	   */
	java.lang.String getNickname();

	/**
	   * <pre>
	   * The logged-in user who made the request.
	   * Most likely, this is the part of the user's email before the `&#64;` sign.  The
	   * field value is the same for different requests from the same user, but
	   * different users can have similar names.  This information is also
	   * available to the application via the App Engine Users API.
	   * This field will be populated starting with App Engine 1.9.21.
	   * </pre>
	   *
	   * <code>string nickname = 40;</code>
	   */
	com.google.protobuf.ByteString getNicknameBytes();

	int URL_MAP_ENTRY_FIELD_NUMBER = 17;

	/**
	   * <pre>
	   * File or class that handled the request.
	   * </pre>
	   *
	   * <code>string url_map_entry = 17;</code>
	   */
	java.lang.String getUrlMapEntry();

	/**
	   * <pre>
	   * File or class that handled the request.
	   * </pre>
	   *
	   * <code>string url_map_entry = 17;</code>
	   */
	com.google.protobuf.ByteString getUrlMapEntryBytes();

	int HOST_FIELD_NUMBER = 20;

	/**
	   * <pre>
	   * Internet host and port number of the resource being requested.
	   * </pre>
	   *
	   * <code>string host = 20;</code>
	   */
	java.lang.String getHost();

	/**
	   * <pre>
	   * Internet host and port number of the resource being requested.
	   * </pre>
	   *
	   * <code>string host = 20;</code>
	   */
	com.google.protobuf.ByteString getHostBytes();

	int COST_FIELD_NUMBER = 21;

	/**
	   * <pre>
	   * An indication of the relative cost of serving this request.
	   * </pre>
	   *
	   * <code>double cost = 21;</code>
	   */
	double getCost();

	int TASK_QUEUE_NAME_FIELD_NUMBER = 22;

	/**
	   * <pre>
	   * Queue name of the request, in the case of an offline request.
	   * </pre>
	   *
	   * <code>string task_queue_name = 22;</code>
	   */
	java.lang.String getTaskQueueName();

	/**
	   * <pre>
	   * Queue name of the request, in the case of an offline request.
	   * </pre>
	   *
	   * <code>string task_queue_name = 22;</code>
	   */
	com.google.protobuf.ByteString getTaskQueueNameBytes();

	int TASK_NAME_FIELD_NUMBER = 23;

	/**
	   * <pre>
	   * Task name of the request, in the case of an offline request.
	   * </pre>
	   *
	   * <code>string task_name = 23;</code>
	   */
	java.lang.String getTaskName();

	/**
	   * <pre>
	   * Task name of the request, in the case of an offline request.
	   * </pre>
	   *
	   * <code>string task_name = 23;</code>
	   */
	com.google.protobuf.ByteString getTaskNameBytes();

	int WAS_LOADING_REQUEST_FIELD_NUMBER = 24;

	/**
	   * <pre>
	   * Whether this was a loading request for the instance.
	   * </pre>
	   *
	   * <code>bool was_loading_request = 24;</code>
	   */
	boolean getWasLoadingRequest();

	int PENDING_TIME_FIELD_NUMBER = 25;

	/**
	   * <pre>
	   * Time this request spent in the pending request queue.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration pending_time = 25;</code>
	   */
	boolean hasPendingTime();

	/**
	   * <pre>
	   * Time this request spent in the pending request queue.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration pending_time = 25;</code>
	   */
	com.google.protobuf.Duration getPendingTime();

	/**
	   * <pre>
	   * Time this request spent in the pending request queue.
	   * </pre>
	   *
	   * <code>.google.protobuf.Duration pending_time = 25;</code>
	   */
	com.google.protobuf.DurationOrBuilder getPendingTimeOrBuilder();

	int INSTANCE_INDEX_FIELD_NUMBER = 26;

	/**
	   * <pre>
	   * If the instance processing this request belongs to a manually scaled
	   * module, then this is the 0-based index of the instance. Otherwise, this
	   * value is -1.
	   * </pre>
	   *
	   * <code>int32 instance_index = 26;</code>
	   */
	int getInstanceIndex();

	int FINISHED_FIELD_NUMBER = 27;

	/**
	   * <pre>
	   * Whether this request is finished or active.
	   * </pre>
	   *
	   * <code>bool finished = 27;</code>
	   */
	boolean getFinished();

	int FIRST_FIELD_NUMBER = 42;

	/**
	   * <pre>
	   * Whether this is the first `RequestLog` entry for this request.  If an
	   * active request has several `RequestLog` entries written to Stackdriver
	   * Logging, then this field will be set for one of them.
	   * </pre>
	   *
	   * <code>bool first = 42;</code>
	   */
	boolean getFirst();

	int INSTANCE_ID_FIELD_NUMBER = 28;

	/**
	   * <pre>
	   * An identifier for the instance that handled the request.
	   * </pre>
	   *
	   * <code>string instance_id = 28;</code>
	   */
	java.lang.String getInstanceId();

	/**
	   * <pre>
	   * An identifier for the instance that handled the request.
	   * </pre>
	   *
	   * <code>string instance_id = 28;</code>
	   */
	com.google.protobuf.ByteString getInstanceIdBytes();

	int LINE_FIELD_NUMBER = 29;

	/**
	   * <pre>
	   * A list of log lines emitted by the application while serving this request.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.LogLine line = 29;</code>
	   */
	java.util.List<com.google.appengine.logging.v1.LogLine> getLineList();

	/**
	   * <pre>
	   * A list of log lines emitted by the application while serving this request.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.LogLine line = 29;</code>
	   */
	java.util.List<? extends com.google.appengine.logging.v1.LogLineOrBuilder> getLineOrBuilderList();

	/**
	   * <pre>
	   * A list of log lines emitted by the application while serving this request.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.LogLine line = 29;</code>
	   */
	int getLineCount();

	/**
	   * <pre>
	   * A list of log lines emitted by the application while serving this request.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.LogLine line = 29;</code>
	   */
	com.google.appengine.logging.v1.LogLine getLine(int index);

	/**
	   * <pre>
	   * A list of log lines emitted by the application while serving this request.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.LogLine line = 29;</code>
	   */
	com.google.appengine.logging.v1.LogLineOrBuilder getLineOrBuilder(int index);

	int APP_ENGINE_RELEASE_FIELD_NUMBER = 38;

	/**
	   * <pre>
	   * App Engine release version.
	   * </pre>
	   *
	   * <code>string app_engine_release = 38;</code>
	   */
	java.lang.String getAppEngineRelease();

	/**
	   * <pre>
	   * App Engine release version.
	   * </pre>
	   *
	   * <code>string app_engine_release = 38;</code>
	   */
	com.google.protobuf.ByteString getAppEngineReleaseBytes();

	int TRACE_ID_FIELD_NUMBER = 39;

	/**
	   * <pre>
	   * Stackdriver Trace identifier for this request.
	   * </pre>
	   *
	   * <code>string trace_id = 39;</code>
	   */
	java.lang.String getTraceId();

	/**
	   * <pre>
	   * Stackdriver Trace identifier for this request.
	   * </pre>
	   *
	   * <code>string trace_id = 39;</code>
	   */
	com.google.protobuf.ByteString getTraceIdBytes();

	int TRACE_SAMPLED_FIELD_NUMBER = 43;

	/**
	   * <pre>
	   * If true, the value in the 'trace_id' field was sampled for storage in a
	   * trace backend.
	   * </pre>
	   *
	   * <code>bool trace_sampled = 43;</code>
	   */
	boolean getTraceSampled();

	int SOURCE_REFERENCE_FIELD_NUMBER = 41;

	/**
	   * <pre>
	   * Source code for the application that handled this request. There can be
	   * more than one source reference per deployed application if source code is
	   * distributed among multiple repositories.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.SourceReference source_reference = 41;</code>
	   */
	java.util.List<com.google.appengine.logging.v1.SourceReference> getSourceReferenceList();

	/**
	   * <pre>
	   * Source code for the application that handled this request. There can be
	   * more than one source reference per deployed application if source code is
	   * distributed among multiple repositories.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.SourceReference source_reference = 41;</code>
	   */
	java.util.List<? extends com.google.appengine.logging.v1.SourceReferenceOrBuilder> getSourceReferenceOrBuilderList();

	/**
	   * <pre>
	   * Source code for the application that handled this request. There can be
	   * more than one source reference per deployed application if source code is
	   * distributed among multiple repositories.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.SourceReference source_reference = 41;</code>
	   */
	int getSourceReferenceCount();

	/**
	   * <pre>
	   * Source code for the application that handled this request. There can be
	   * more than one source reference per deployed application if source code is
	   * distributed among multiple repositories.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.SourceReference source_reference = 41;</code>
	   */
	com.google.appengine.logging.v1.SourceReference getSourceReference(int index);

	/**
	   * <pre>
	   * Source code for the application that handled this request. There can be
	   * more than one source reference per deployed application if source code is
	   * distributed among multiple repositories.
	   * </pre>
	   *
	   * <code>repeated .google.appengine.logging.v1.SourceReference source_reference = 41;</code>
	   */
	com.google.appengine.logging.v1.SourceReferenceOrBuilder getSourceReferenceOrBuilder(int index);

	boolean isInitialized();

	void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException;

	int getSerializedSize();

	boolean equals(java.lang.Object obj);

	int hashCode();

	Builder newBuilderForType();

	Builder toBuilder();

	com.google.protobuf.Parser<RequestLog> getParserForType();

	RequestLogOrBuilder getDefaultInstanceForType();

}