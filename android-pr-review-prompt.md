# 🔍 Android PR Review - Expert Architect Mode

> **Auto-detected**: Copilot loads this via `.github/copilot-instructions.md`  
> **Rules**: [android-pr-review-rules.md](android-pr-review-rules.md)

---

> 💡 **HOW TO USE:** Supported commands and input formats for triggering a PR review.

## USAGE

```
Review PR: <pr-url>
Review PR: <pr-url1> <pr-url2> <pr-url3>  (multiple PRs)
```

**Examples**:

```
Review PR: https://github.com/pvelangani02-del/MVVM-Architecture-Android/pull/1
Review PR: https://github.com/org/repo/pull/1 https://github.com/org/repo/pull/2
```

**Works with**:

- ✅ OPEN PRs (not yet merged)
- ✅ MERGED PRs (closed)
- ✅ Multiple PRs at once

---

> 💡 **WORKFLOW:** Step-by-step process the reviewer follows from locating the repo to generating the report.

## EXECUTION PROCESS


**CRITICAL**: System automatically detects if PR is OPEN or MERGED. No user action needed.

> 💡 **STEP 1:** Find the Android project root in the workspace.

### Step 1: Locate Repository

Check workspace for Android project structure:
- Look for `build.gradle.kts` or `build.gradle` files
- Check for `app/src/main/` directory structure
- Identify `AndroidManifest.xml` location

> 💡 **STEP 2:** Determine whether the PR is open or closed.

### Step 2: Parse PR & Find Branch

**For PR URLs**: Extract source and target branches from the GitHub PR page (e.g., `"wants to merge into develop from feature"`).

**For explicit branches**: Use the branches provided by the user (e.g., `"Review feature against develop"`).

```bash
cd ${REPO_PATH}
git fetch origin

# Check if PR is MERGED (has merge commit)
MERGE_COMMIT=$(git log --oneline --all | grep -i "pull request #${PR_NUMBER}" | head -1)

if [ -n "$MERGE_COMMIT" ]; then
  # MERGED PR: Extract branches from merge commit
  COMMIT_HASH=$(echo $MERGE_COMMIT | awk '{print $1}')
  PARENTS=$(git show --format="%P" --no-patch $COMMIT_HASH)
else
  # OPEN PR: Use source/target from PR page
  SOURCE_BRANCH=<from_pr_page>
  TARGET_BRANCH=<from_pr_page>
fi
```

> 💡 **STEP 3:** Compute merge-base and generate the diff for review.

### Step 3: Get Accurate Diff

**For MERGED PRs** (has merge commit):

```bash
MERGE_BASE=$(git merge-base <parent1> <parent2>)
git diff ${MERGE_BASE}..<source_parent> --stat
git diff ${MERGE_BASE}..<source_parent>
```

**For OPEN PRs** (no merge commit yet):

```bash
TARGET=<detected_target_branch>
SOURCE=<detected_branch>

MERGE_BASE=$(git merge-base origin/${TARGET} origin/${SOURCE})

# Get complete diff
git diff ${MERGE_BASE}..origin/${SOURCE} --stat
git diff ${MERGE_BASE}..origin/${SOURCE}

# Count actual code commits
git log --oneline ${MERGE_BASE}..origin/${SOURCE} --no-merges
```

> 💡 **STEP 4:** Evaluate the diff against the rules checklist from the rules file.

### Step 4: Apply Android-Specific Rules

Read ALL rules from [android-pr-review-rules.md](android-pr-review-rules.md) and check against changes.
Evaluate every checklist item and include the **Rules Checklist** section in the output report with ✅ Pass, ❌ Fail, or ⚠️ N/A per item.

> 💡 **STEP 5:** Produce the final review report using the template below.

### Step 5: Generate Balanced Report

**DEFAULT OUTPUT** (concise but thorough):

```
## 🔍 PR #XXX: android-app-name

**Branch:** source → target
**Files:** X | **+X/-X** | **Commits:** X | **Rating: X/5**

### 📋 Summary
Purpose: Brief description of what PR does
Risk: 🟢 LOW | 🟡 MEDIUM | 🔴 HIGH
UI Impact: Yes/No | Performance Impact: Yes/No

Branch Name: ✅ Follows convention | ❌ Does not follow convention (explain)

Expected branch naming patterns:
- `feature/<ticket-id>-short-description` (e.g., feature/JIRA-123-add-login)
- `bugfix/<ticket-id>-short-description`
- `hotfix/<ticket-id>-short-description`
- `release/<version>` (e.g., release/1.2.0)

Flag if: branch uses generic names (e.g., `feature`, `feature-2`, `improvements`), missing ticket ID, or not following kebab-case.

### 🚨 Critical Issues (X)
1. **[file:line] Issue Title**
   - Problem: What's wrong
   - Impact: Why it matters (crash, security, data loss)
   - Android: Specific Android framework concern

### ⚠️ Warnings (X)
1. **[file] Issue Title** - Brief description with impact

### ✅ Positives (X)
1. What was done well (only if noteworthy)

### 📱 Android Specific
- Dependencies: Major changes listed
- UI Changes: Screens affected

### ✅ Rules Checklist
Read all checklist items from [android-pr-review-rules.md](android-pr-review-rules.md) and evaluate each one against the PR changes. Reproduce every item exactly as listed in the rules file, marking each as:
- ✅ Pass — PR satisfies this check
- ❌ Fail — PR violates this check (add brief reason)
- ⚠️ N/A — Not applicable to this PR's changes

Preserve the original section groupings (Functionality, Code Quality, Formatting, Testing, Documentation) from the rules file.

### 🎯 Decision
✅ APPROVE - Clean implementation, follows Android best practices
⚠️ CONDITIONAL - Fix X before merge (minor issues)
🔴 BLOCK - Critical issues: X, Y (security/crash risk)
```

> 💡 **OUTPUT RULES:** Controls what to include/exclude in the review output.

    **OUTPUT RULES**:

- **Critical issues**: Include file:line, problem, impact, Android-specific concern
- **Warnings**: 1 line with file reference and description
- **Positives**: Only mention exceptional Android code quality
- **NO code snippets** unless user asks "show fix" or "how to fix"
- **NO domain explanations** - user knows Android
- **NO "Rule:" citations** - just state issues clearly
- **Android context**: Mention SDK versions, architecture, UI impact

> 💡 **ON-DEMAND DETAIL:** Only shown when the user explicitly asks for fixes or explanations.

**WHEN USER ASKS** for details:

- Show before/after code examples with Android context
- Detailed explanation with Android framework reasoning
- References to Android guidelines violated
- Step-by-step fix instructions with Android APIs

---

> 💡 **SEVERITY GUIDE:** Defines what counts as Critical, Warning, or Positive to keep reviews consistent.

## 🎯 ISSUE SEVERITY GUIDE

**Critical (Block Merge)**:
- Security vulnerabilities (hardcoded keys, unencrypted data, exported components)
- Crash risks (NPE, memory leaks, ANR potential)
- Data loss scenarios (missing null checks, improper lifecycle handling)
- UI breaking changes (incompatible layouts, missing resources)
- Performance killers (main thread blocking, unoptimized queries)
- API contract breaks (changed data models, removed public methods)

**Warnings**:
- Code smells (magic numbers, hardcoded strings not in resources)
- Missing validations (null checks, input validation)
- Performance concerns (inefficient adapters, missing ViewHolder pattern)
- Architecture violations (business logic in Activities/Fragments)
- Memory concerns (static context references, unclosed resources)
- Accessibility issues (missing content descriptions, touch target sizes)
- Testing gaps (missing unit tests for ViewModels/UseCases)

**Positives** (only mention if exceptional):
- Excellent lifecycle management
- Proper coroutine scope handling
- Clean architecture separation
- Comprehensive error handling
- Good accessibility implementation
- Proper dependency injection

---

> 💡 **COMPLEXITY GUIDE:** Helps the reviewer gauge PR risk based on scope and lines changed.

## 📊 COMPLEXITY ASSESSMENT

**Low Complexity** 🟢:
- UI-only changes (layouts, colors, strings)
- Simple bug fixes
- Code refactoring without logic changes
- Resource additions
- < 100 lines changed

**Medium Complexity** 🟡:
- New feature with standard patterns
- Database changes with proper migration
- API integration following existing patterns
- Architecture changes in single module
- 100-500 lines changed

**High Complexity** 🔴:
- Major architectural changes
- Multi-module refactoring
- New third-party library integration
- Security-critical features
- Payment/financial logic
- > 500 lines changed

---

**Maintained by**: Android Development Team | **Questions**: #android-dev Slack

